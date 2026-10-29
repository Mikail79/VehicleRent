package id.ac.stmi.vehiclerent

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.ac.stmi.vehiclerent.adapter.VehicleAdapter
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import id.ac.stmi.vehiclerent.util.BottomNavHelper
import id.ac.stmi.vehiclerent.util.MotionUtils
import id.ac.stmi.vehiclerent.util.ProfileDialogHelper
import id.ac.stmi.vehiclerent.util.VehicleRentPrefs
import java.text.NumberFormat
import java.util.Locale

class BerandaFleetActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    private lateinit var tvLiveBayStatus: TextView
    private lateinit var tvTotalFleetCount: TextView
    private lateinit var tvMetricReadyCount: TextView
    private lateinit var tvMetricRentedCount: TextView
    private lateinit var tvMetricWorkshopCount: TextView
    private lateinit var tvMetricWorkshopSub: TextView

    private lateinit var cardFilterReady: LinearLayout
    private lateinit var cardFilterRented: LinearLayout
    private lateinit var cardFilterWorkshop: LinearLayout

    private lateinit var btnToggleBundling: Button
    private lateinit var btnToggleSingleCar: Button
    private lateinit var spinnerCategory: Spinner
    private lateinit var rgDriverMode: RadioGroup
    private lateinit var rbSelfDrive: RadioButton
    private lateinit var rbWithDriver: RadioButton

    private lateinit var llBundleContainer: LinearLayout
    private lateinit var cbFamilyTouring: CheckBox
    private lateinit var cbDashcamGps: CheckBox
    private lateinit var cbCoolerBox: CheckBox

    private lateinit var llVehicleContainer: LinearLayout

    private var isBundlingMode = true
    private var selectedCategory = "Semua Kategori"
    private var activeStatusFilter: VehicleStatus? = null

    private var previousReadyCount: Int = -1
    private var previousRentedCount: Int = -1
    private var previousWorkshopCount: Int = -1

    private var contextMenuVehicle: VehicleModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        ProfileDialogHelper.applyDarkMode(prefs.isDarkMode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda_fleet)

        initViews()
        setupCategorySpinner()
        setupEventListeners()
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        ProfileDialogHelper.setupRoleBanner(this) {
            refreshFleetData()
            setupBottomNav()
        }
        setupBottomNav()
        refreshFleetData()
    }

    private fun initViews() {
        tvLiveBayStatus = findViewById(R.id.tvLiveBayStatus)
        tvTotalFleetCount = findViewById(R.id.tvTotalFleetCount)
        tvMetricReadyCount = findViewById(R.id.tvMetricReadyCount)
        tvMetricRentedCount = findViewById(R.id.tvMetricRentedCount)
        tvMetricWorkshopCount = findViewById(R.id.tvMetricWorkshopCount)
        tvMetricWorkshopSub = findViewById(R.id.tvMetricWorkshopSub)

        cardFilterReady = findViewById(R.id.cardFilterReady)
        cardFilterRented = findViewById(R.id.cardFilterRented)
        cardFilterWorkshop = findViewById(R.id.cardFilterWorkshop)

        btnToggleBundling = findViewById(R.id.btnToggleBundling)
        btnToggleSingleCar = findViewById(R.id.btnToggleSingleCar)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        rgDriverMode = findViewById(R.id.rgDriverMode)
        rbSelfDrive = findViewById(R.id.rbSelfDrive)
        rbWithDriver = findViewById(R.id.rbWithDriver)

        llBundleContainer = findViewById(R.id.llBundleContainer)
        cbFamilyTouring = findViewById(R.id.cbFamilyTouring)
        cbDashcamGps = findViewById(R.id.cbDashcamGps)
        cbCoolerBox = findViewById(R.id.cbCoolerBox)

        llVehicleContainer = findViewById(R.id.llVehicleContainer)

        MotionUtils.applyTactilePressFeedback(cardFilterReady)
        MotionUtils.applyTactilePressFeedback(cardFilterRented)
        MotionUtils.applyTactilePressFeedback(cardFilterWorkshop)
        MotionUtils.applyTactilePressFeedback(btnToggleBundling)
        MotionUtils.applyTactilePressFeedback(btnToggleSingleCar)

        findViewById<FrameLayout>(R.id.btnToolbarProfile).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this) {
                refreshFleetData()
            }
        }

        findViewById<ImageButton>(R.id.btnToolbarMore).setOnClickListener { view ->
            showOptionsMenuPopup(view)
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf(
            "Semua Kategori",
            "MPV Keluarga (Zenix / Xpander)",
            "Premium SUV (Fortuner / Pajero)",
            "City Car & EV (Ioniq 5 / Brio)"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = spinnerAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                renderVehicleCards()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupEventListeners() {
        btnToggleBundling.setOnClickListener {
            isBundlingMode = true
            btnToggleBundling.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_navy))
            btnToggleBundling.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnToggleSingleCar.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            btnToggleSingleCar.setTextColor(ContextCompat.getColor(this, R.color.text_slate_600))
            llBundleContainer.visibility = View.VISIBLE
        }

        btnToggleSingleCar.setOnClickListener {
            isBundlingMode = false
            btnToggleSingleCar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_navy))
            btnToggleSingleCar.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnToggleBundling.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            btnToggleBundling.setTextColor(ContextCompat.getColor(this, R.color.text_slate_600))
            llBundleContainer.visibility = View.GONE
        }

        cardFilterReady.setOnClickListener {
            activeStatusFilter = if (activeStatusFilter == VehicleStatus.READY) null else VehicleStatus.READY
            renderVehicleCards()
        }

        cardFilterRented.setOnClickListener {
            activeStatusFilter = if (activeStatusFilter == VehicleStatus.ON_RENT) null else VehicleStatus.ON_RENT
            renderVehicleCards()
        }

        cardFilterWorkshop.setOnClickListener {
            activeStatusFilter = if (activeStatusFilter == VehicleStatus.IN_WORKSHOP) null else VehicleStatus.IN_WORKSHOP
            renderVehicleCards()
        }
    }

    private fun refreshFleetData() {
        tvLiveBayStatus.text = "Update armada langsung • ${prefs.stagingBay} | ${prefs.staffName}"

        val fleet = prefs.getFleetList()
        val readyCount = fleet.count { it.status == VehicleStatus.READY }
        val rentedCount = fleet.count { it.status == VehicleStatus.ON_RENT }
        val workshopCount = fleet.count { it.status == VehicleStatus.IN_WORKSHOP }

        tvTotalFleetCount.text = "Total: ${fleet.size} Unit"

        // Numeric rolling counter (300ms ease-out)
        if (previousReadyCount < 0) {
            tvMetricReadyCount.text = readyCount.toString()
            tvMetricRentedCount.text = rentedCount.toString()
            tvMetricWorkshopCount.text = workshopCount.toString()
        } else {
            MotionUtils.animateIntRolling(tvMetricReadyCount, previousReadyCount, readyCount)
            MotionUtils.animateIntRolling(tvMetricRentedCount, previousRentedCount, rentedCount)
            MotionUtils.animateIntRolling(tvMetricWorkshopCount, previousWorkshopCount, workshopCount)
        }
        previousReadyCount = readyCount
        previousRentedCount = rentedCount
        previousWorkshopCount = workshopCount

        val workshopBays = fleet.filter { it.status == VehicleStatus.IN_WORKSHOP }
            .map { it.stagingBay.replace("Bay ", "") }
            .take(2)
            .joinToString(",")
        tvMetricWorkshopSub.text = if (workshopBays.isNotEmpty()) "Bay $workshopBays" else "Bay 02,04"

        renderVehicleCards()
    }

    private fun renderVehicleCards() {
        llVehicleContainer.removeAllViews()

        val fleet = prefs.getFleetList()
        val filteredList = fleet.filter { vehicle ->
            val matchCategory = (selectedCategory == "Semua Kategori" || vehicle.category == selectedCategory)
            val matchStatus = (activeStatusFilter == null || vehicle.status == activeStatusFilter)
            matchCategory && matchStatus
        }

        if (filteredList.isEmpty()) {
            val emptyCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setBackgroundResource(R.drawable.bg_card_surface)
                setPadding(40, 56, 40, 56)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 32)
                }
            }

            val icon = android.widget.ImageView(this).apply {
                setImageResource(R.drawable.ic_directions_car)
                setColorFilter(ContextCompat.getColor(context, R.color.text_slate_400))
                layoutParams = LinearLayout.LayoutParams(64, 64)
            }

            val title = TextView(this).apply {
                text = "Tidak Ada Unit Armada Sesuai Filter"
                setTextAppearance(R.style.TextAppearance_HeadingMD)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 16, 0, 4)
            }

            val subtitle = TextView(this).apply {
                text = "Tidak ditemukan armada untuk kategori \"$selectedCategory\" atau filter status yang dipilih."
                setTextAppearance(R.style.TextAppearance_BodySM)
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.text_slate_600))
            }

            val btnReset = Button(this).apply {
                text = "Reset Filter"
                setBackgroundResource(R.drawable.bg_button_primary)
                setTextColor(ContextCompat.getColor(context, R.color.white))
                textSize = 13f
                isAllCaps = false
                val btnLp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    100
                ).apply {
                    setMargins(0, 24, 0, 0)
                }
                layoutParams = btnLp
                MotionUtils.applyTactilePressFeedback(this)
                setOnClickListener {
                    selectedCategory = "Semua Kategori"
                    activeStatusFilter = null
                    spinnerCategory.setSelection(0)
                    refreshFleetData()
                }
            }

            emptyCard.addView(icon)
            emptyCard.addView(title)
            emptyCard.addView(subtitle)
            emptyCard.addView(btnReset)
            llVehicleContainer.addView(emptyCard)
            return
        }

        val adapter = VehicleAdapter(
            context = this,
            vehicleList = filteredList,
            onBookClicked = { vehicle ->
                handleVehicleAction(vehicle)
            },
            onItemLongClicked = { vehicle, view ->
                contextMenuVehicle = vehicle
                view.showContextMenu()
                true
            }
        )

        for (i in 0 until adapter.count) {
            val itemView = adapter.getView(i, null, llVehicleContainer)
            registerForContextMenu(itemView)
            itemView.setOnLongClickListener {
                contextMenuVehicle = adapter.getItem(i)
                showVehicleContextMenu(it, adapter.getItem(i))
                true
            }
            llVehicleContainer.addView(itemView)
        }
    }

    private fun handleVehicleAction(vehicle: VehicleModel) {
        when (vehicle.status) {
            VehicleStatus.READY -> {
                val intent = Intent(this, FormPemesananActivity::class.java).apply {
                    putExtra(EXTRA_VEHICLE, vehicle)
                    putExtra(EXTRA_WITH_DRIVER, rbWithDriver.isChecked)
                    putExtra(EXTRA_IS_BUNDLING, isBundlingMode)
                    putExtra(EXTRA_FAMILY_TOURING, isBundlingMode && cbFamilyTouring.isChecked)
                    putExtra(EXTRA_DASHCAM_GPS, isBundlingMode && cbDashcamGps.isChecked)
                    putExtra(EXTRA_COOLER_BOX, isBundlingMode && cbCoolerBox.isChecked)
                }
                startActivity(intent)
            }
            VehicleStatus.ON_RENT -> {
                val intent = Intent(this, InspeksiReturnActivity::class.java).apply {
                    putExtra(EXTRA_VEHICLE, vehicle)
                }
                startActivity(intent)
            }
            VehicleStatus.IN_WORKSHOP -> {
                if (prefs.isWorkshopAdmin) {
                    val intent = Intent(this, StallBengkelActivity::class.java).apply {
                        putExtra("EXTRA_VEHICLE_ID", vehicle.id)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Unit ${vehicle.plateNumber} sedang dalam servis bengkel internal (${vehicle.workshopTargetTime}).", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showVehicleContextMenu(anchorView: View, vehicle: VehicleModel) {
        val popup = PopupMenu(this, anchorView)
        popup.menu.add(0, 1, 0, "1. Pindahkan ke Bengkel Internal")
        popup.menu.add(0, 2, 1, "2. Tandai Lulus QC (Siap Sewa)")
        popup.menu.add(0, 3, 2, "3. Lihat Riwayat Odometer & Servis")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    prefs.updateVehicleStatus(
                        id = vehicle.id,
                        newStatus = VehicleStatus.IN_WORKSHOP,
                        workshopHours = 3.5,
                        targetTime = "Besok 14:00 WIB"
                    )
                    Toast.makeText(this, "${vehicle.name} (${vehicle.plateNumber}) berhasil dipindahkan ke Bengkel Internal!", Toast.LENGTH_LONG).show()
                    refreshFleetData()
                    true
                }
                2 -> {
                    prefs.updateVehicleStatus(
                        id = vehicle.id,
                        newStatus = VehicleStatus.READY,
                        workshopHours = 0.0,
                        targetTime = "-"
                    )
                    Toast.makeText(this, "${vehicle.name} (${vehicle.plateNumber}) ditandai LULUS QC (Siap Sewa)!", Toast.LENGTH_LONG).show()
                    refreshFleetData()
                    true
                }
                3 -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Riwayat Odometer & Servis")
                        .setIcon(R.drawable.ic_speed)
                        .setMessage(
                            "Unit: ${vehicle.name}\n" +
                            "Plat Nomor: ${vehicle.plateNumber}\n" +
                            "Odometer Terkini: ${currencyFormat.format(vehicle.lastOdometer)} km\n" +
                            "Staging: ${vehicle.stagingBay}\n" +
                            "Status: ${vehicle.status.label}\n\n" +
                            "Catatan Servis:\n${vehicle.lastCheckNotes}"
                        )
                        .setPositiveButton("Tutup", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showOptionsMenuPopup(anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menu.add(0, 1, 0, "Profil & Pengaturan Petugas")
        popup.menu.add(0, 2, 1, "Ubah Mode Tampilan (Dark/Light)")
        popup.menu.add(0, 3, 2, "Reset Data & Sesi (Clear SharedPreferences)")
        popup.menu.add(0, 4, 3, "Tentang AutoFleet Direct")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    ProfileDialogHelper.showProfileDialog(this) { refreshFleetData() }
                    true
                }
                2 -> {
                    val newMode = !prefs.isDarkMode
                    prefs.isDarkMode = newMode
                    ProfileDialogHelper.applyDarkMode(newMode)
                    true
                }
                3 -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Konfirmasi Reset")
                        .setMessage("Apakah Anda yakin ingin menghapus seluruh data SharedPreferences?")
                        .setPositiveButton("Reset") { _, _ ->
                            prefs.clearAndResetAll()
                            Toast.makeText(this, "Data berhasil di-reset!", Toast.LENGTH_SHORT).show()
                            refreshFleetData()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                    true
                }
                4 -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("AutoFleet Direct (VehicleRent)")
                        .setMessage("Aplikasi Manajemen Armada Rental Mobil & Bengkel Internal.\nMencakup materi lengkap Pertemuan 1 - 7 Pemrograman Mobile.")
                        .setPositiveButton("OK", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupBottomNav() {
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.BERANDA)
    }

    companion object {
        const val EXTRA_VEHICLE = "EXTRA_VEHICLE"
        const val EXTRA_WITH_DRIVER = "EXTRA_WITH_DRIVER"
        const val EXTRA_IS_BUNDLING = "EXTRA_IS_BUNDLING"
        const val EXTRA_FAMILY_TOURING = "EXTRA_FAMILY_TOURING"
        const val EXTRA_DASHCAM_GPS = "EXTRA_DASHCAM_GPS"
        const val EXTRA_COOLER_BOX = "EXTRA_COOLER_BOX"
    }
}

