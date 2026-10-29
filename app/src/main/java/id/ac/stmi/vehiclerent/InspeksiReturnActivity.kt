package id.ac.stmi.vehiclerent

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.ac.stmi.vehiclerent.model.ReservationModel
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import id.ac.stmi.vehiclerent.util.BottomNavHelper
import id.ac.stmi.vehiclerent.util.MotionUtils
import id.ac.stmi.vehiclerent.util.ProfileDialogHelper
import id.ac.stmi.vehiclerent.util.VehicleRentPrefs
import java.text.NumberFormat
import java.util.Locale

class InspeksiReturnActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    private var activeVehicle: VehicleModel? = null
    private var initialOdo: Int = 24120

    private lateinit var tvReturnCarPlate: TextView
    private lateinit var etReturnOdometer: EditText
    private lateinit var tvAutoTripCalculated: TextView
    private lateinit var tvErrorOdometer: TextView
    private lateinit var spinnerReturnFuel: Spinner
    private lateinit var tvFuelPenaltyCalculated: TextView

    private lateinit var spinnerWorkshopCategory: Spinner
    private lateinit var etObdCode: EditText
    private lateinit var tvObdBadge: TextView

    private lateinit var cbFindingBumper: CheckBox
    private lateinit var cbFindingBrake: CheckBox
    private lateinit var cbFindingEngine: CheckBox
    private lateinit var cbFindingOilService: CheckBox

    private lateinit var cardWorkshopEstimates: LinearLayout
    private lateinit var tvWorkshopAutoStatusTitle: TextView
    private lateinit var tvWorkshopAutoStatusSubtitle: TextView
    private lateinit var tvWorkshopEstHours: TextView
    private lateinit var tvWorkshopTargetReady: TextView
    private lateinit var tvClaimItemsSummary: TextView
    private lateinit var tvWorkshopTotalClaimCost: TextView

    private lateinit var btnSubmitToWorkshop: Button
    private lateinit var btnShareClaimReport: Button

    private var currentFuelPenalty = 180000L
    private var totalEstHours = 5.5
    private var totalEstCost = 1130000L
    private var previousEstCost: Long = 0L
    private var previousEstHours: Double = 0.0
    private var isWorkshopBranchingActive: Boolean? = null
    private var isUserManuallyToggledOil = false

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspeksi_return)

        loadTargetVehicle()
        initViews()
        setupFuelSpinner()
        setupWorkshopCategorySpinner()
        setupListeners()
        recalculateWorkshopEstimates()
    }

    private fun loadTargetVehicle() {
        val extraRes = intent.getSerializableExtra(DetailReservasiActivity.EXTRA_RESERVATION) as? ReservationModel
        val extraVeh = intent.getSerializableExtra(BerandaFleetActivity.EXTRA_VEHICLE) as? VehicleModel

        activeVehicle = extraRes?.vehicle
            ?: extraVeh
            ?: prefs.getFleetList().firstOrNull { it.status == VehicleStatus.ON_RENT }
            ?: prefs.getVehicleById("AF-01")
            ?: prefs.getFleetList().firstOrNull()

        initialOdo = prefs.initialOdometer
        if (initialOdo <= 0) initialOdo = 24120
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<FrameLayout>(R.id.btnToolbarProfile).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this)
        }

        tvReturnCarPlate = findViewById(R.id.tvReturnCarPlate)
        etReturnOdometer = findViewById(R.id.etReturnOdometer)
        tvAutoTripCalculated = findViewById(R.id.tvAutoTripCalculated)
        tvErrorOdometer = findViewById(R.id.tvErrorOdometer)
        spinnerReturnFuel = findViewById(R.id.spinnerReturnFuel)
        tvFuelPenaltyCalculated = findViewById(R.id.tvFuelPenaltyCalculated)

        spinnerWorkshopCategory = findViewById(R.id.spinnerWorkshopCategory)
        etObdCode = findViewById(R.id.etObdCode)
        tvObdBadge = findViewById(R.id.tvObdBadge)

        cbFindingBumper = findViewById(R.id.cbFindingBumper)
        cbFindingBrake = findViewById(R.id.cbFindingBrake)
        cbFindingEngine = findViewById(R.id.cbFindingEngine)
        cbFindingOilService = findViewById(R.id.cbFindingOilService)

        cardWorkshopEstimates = findViewById(R.id.cardWorkshopEstimates)
        tvWorkshopAutoStatusTitle = findViewById(R.id.tvWorkshopAutoStatusTitle)
        tvWorkshopAutoStatusSubtitle = findViewById(R.id.tvWorkshopAutoStatusSubtitle)
        tvWorkshopEstHours = findViewById(R.id.tvWorkshopEstHours)
        tvWorkshopTargetReady = findViewById(R.id.tvWorkshopTargetReady)
        tvClaimItemsSummary = findViewById(R.id.tvClaimItemsSummary)
        tvWorkshopTotalClaimCost = findViewById(R.id.tvWorkshopTotalClaimCost)

        btnSubmitToWorkshop = findViewById(R.id.btnSubmitToWorkshop)
        btnShareClaimReport = findViewById(R.id.btnShareClaimReport)

        // Setup Bottom Nav and Tactile Feedback
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.BENGKEL)
        MotionUtils.applyTactilePressFeedback(btnSubmitToWorkshop)
        MotionUtils.applyTactilePressFeedback(btnShareClaimReport)

        val v = activeVehicle!!
        tvReturnCarPlate.text = "${v.name} • ${v.plateNumber}"
    }

    private fun setupFuelSpinner() {
        val fuelOptions = listOf(
            "75% (Minus 25% - Denda Rp 180.000)",
            "100% Full Tank (Sesuai Standar - Rp 0)",
            "50% (Minus 50% - Denda Rp 350.000)",
            "25% (Minus 75% - Denda Rp 500.000)"
        )
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fuelOptions)
        spinnerReturnFuel.adapter = spAdapter

        spinnerReturnFuel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFuelPenalty = when (position) {
                    0 -> 180000L
                    1 -> 0L
                    2 -> 350000L
                    3 -> 500000L
                    else -> 0L
                }
                tvFuelPenaltyCalculated.text = if (currentFuelPenalty > 0) {
                    tvFuelPenaltyCalculated.setTextColor(ContextCompat.getColor(this@InspeksiReturnActivity, R.color.status_workshop_text))
                    "Penalti BBM: Rp ${currencyFormat.format(currentFuelPenalty)}"
                } else {
                    tvFuelPenaltyCalculated.setTextColor(ContextCompat.getColor(this@InspeksiReturnActivity, R.color.status_ready_text))
                    "BBM Penuh (Tanpa Denda)"
                }
                recalculateWorkshopEstimates()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupWorkshopCategorySpinner() {
        val categories = listOf(
            "Bodi & Cat (Body Repair)",
            "Mesin & OBD-II",
            "Rem & Kaki-Kaki",
            "AC & Kelistrikan"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerWorkshopCategory.adapter = adapter
    }

    private fun setupListeners() {
        // Odometer calculation
        etReturnOdometer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val returnOdo = s.toString().toIntOrNull() ?: initialOdo
                if (returnOdo < initialOdo) {
                    tvErrorOdometer.visibility = View.VISIBLE
                    tvErrorOdometer.text = "Odometer akhir ($returnOdo km) tidak boleh lebih rendah dari awal ($initialOdo km)"
                } else {
                    tvErrorOdometer.visibility = View.GONE
                }

                val trip = if (returnOdo >= initialOdo) returnOdo - initialOdo else 0
                tvAutoTripCalculated.text = "Trip: $trip km (Auto)"

                // Auto check oil change if trip > 500 km or returnOdo >= 24800 km
                if (!isUserManuallyToggledOil && (trip > 500 || returnOdo >= 24800)) {
                    cbFindingOilService.isChecked = true
                }
                recalculateWorkshopEstimates()
            }
        })

        // OBD Code Listener
        etObdCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val code = s.toString().trim().uppercase()
                when {
                    code.contains("P0300") -> {
                        tvObdBadge.text = "MISFIRE (+2.0 Jam / Rp 450k)"
                        tvObdBadge.visibility = View.VISIBLE
                    }
                    code.contains("C0035") -> {
                        tvObdBadge.text = "ABS SENSOR (+1.5 Jam / Rp 350k)"
                        tvObdBadge.visibility = View.VISIBLE
                    }
                    code.isEmpty() -> {
                        tvObdBadge.text = "NORMAL (Rp 0)"
                        tvObdBadge.visibility = View.GONE
                    }
                    else -> {
                        tvObdBadge.text = "FAULT (+1.0 Jam / Rp 250k)"
                        tvObdBadge.visibility = View.VISIBLE
                    }
                }
                recalculateWorkshopEstimates()
            }
        })

        // Checkbox listeners
        val checkListener = View.OnClickListener {
            recalculateWorkshopEstimates()
        }
        cbFindingBumper.setOnClickListener(checkListener)
        cbFindingBrake.setOnClickListener(checkListener)
        cbFindingEngine.setOnClickListener(checkListener)
        cbFindingOilService.setOnClickListener {
            isUserManuallyToggledOil = true
            recalculateWorkshopEstimates()
        }

        // Primary Button: Kirim ke Bengkel Internal (Buka Tiket Servis)
        btnSubmitToWorkshop.setOnClickListener {
            showConfirmationDialog()
        }

        // Secondary Button: Kirim Laporan Klaim ke Penyewa via Intent
        btnShareClaimReport.setOnClickListener {
            shareClaimReport()
        }
    }

    private fun recalculateWorkshopEstimates() {
        var hours = 0.0
        var repairCost = 0L
        val activeItems = mutableListOf<String>()

        val obd = etObdCode.text.toString().trim().uppercase()
        when {
            obd.contains("P0300") -> {
                hours += 2.0
                repairCost += 450000L
                activeItems.add("ECU Misfire P0300")
            }
            obd.contains("C0035") -> {
                hours += 1.5
                repairCost += 350000L
                activeItems.add("ABS Sensor C0035")
            }
            obd.isNotEmpty() -> {
                hours += 1.0
                repairCost += 250000L
                activeItems.add("Diagnostik $obd")
            }
        }

        if (cbFindingBumper.isChecked) {
            hours += 3.5
            repairCost += 600000L
            activeItems.add("Baret Panel Bumper")
        }

        if (cbFindingBrake.isChecked) {
            hours += 2.0
            repairCost += 350000L
            activeItems.add("Kampas Rem Depan")
        }

        if (cbFindingEngine.isChecked) {
            hours += 2.5
            repairCost += 500000L
            activeItems.add("Check Engine Permanen")
        }

        if (cbFindingOilService.isChecked) {
            hours += 1.5
            repairCost += 450000L
            activeItems.add("Ganti Oli Berkala")
        }

        if (currentFuelPenalty > 0) {
            activeItems.add("Penalti BBM")
        }

        totalEstHours = hours
        totalEstCost = repairCost + currentFuelPenalty

        // Numeric rolling counter (300ms ease-out)
        if (previousEstCost == 0L && totalEstCost == 0L) {
            tvWorkshopTotalClaimCost.text = "Rp ${currencyFormat.format(totalEstCost)}"
        } else {
            MotionUtils.animateNumberRolling(tvWorkshopTotalClaimCost, previousEstCost, totalEstCost)
        }
        previousEstCost = totalEstCost

        MotionUtils.animateDoubleRolling(tvWorkshopEstHours, previousEstHours, totalEstHours)
        previousEstHours = totalEstHours

        tvClaimItemsSummary.text = if (activeItems.isNotEmpty()) {
            activeItems.joinToString(" + ")
        } else {
            "Kondisi Normal (Bebas Biaya Perbaikan)"
        }

        // Two-way status branching & Pulse Animation
        val isCurrentlyWorkshop = (activeItems.isNotEmpty() && (hours > 0 || totalEstCost > 0))
        if (isWorkshopBranchingActive != isCurrentlyWorkshop) {
            val fromColor = if (isCurrentlyWorkshop) ContextCompat.getColor(this, R.color.status_ready_text) else ContextCompat.getColor(this, R.color.workshop_amber)
            val toColor = if (isCurrentlyWorkshop) ContextCompat.getColor(this, R.color.workshop_amber) else ContextCompat.getColor(this, R.color.status_ready_text)
            val fillColor = ContextCompat.getColor(this, R.color.workshop_amber_surface)
            MotionUtils.animateBorderColor(cardWorkshopEstimates, fromColor, toColor, fillColor, strokeWidthPx = 3, cornerRadiusPx = 32f, duration = 250L)
            isWorkshopBranchingActive = isCurrentlyWorkshop
        }

        if (isCurrentlyWorkshop) {
            tvWorkshopAutoStatusTitle.text = "MASUK ANTREAN BENGKEL INTERNAL"
            tvWorkshopAutoStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.status_workshop_text))
            tvWorkshopAutoStatusSubtitle.text = "Unit otomatis DIKUNCI dari katalog sewa publik hingga selesai inspeksi akhir."
            tvWorkshopTargetReady.text = "Besok 14:00 WIB (Stall #03)"
            btnSubmitToWorkshop.text = "Kirim ke Bengkel Internal (Buka Tiket Servis)"
            btnSubmitToWorkshop.setBackgroundResource(R.drawable.bg_button_amber)
        } else {
            tvWorkshopAutoStatusTitle.text = "LULUS INSPEKSI RETURN"
            tvWorkshopAutoStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))
            tvWorkshopAutoStatusSubtitle.text = "Unit langsung SIAP SEWA kembali tanpa antrean bengkel internal."
            tvWorkshopTargetReady.text = "Hari ini (Langsung Siap)"
            btnSubmitToWorkshop.text = "Tandai Selesai & Lulus QC (Siap Sewa)"
            btnSubmitToWorkshop.setBackgroundResource(R.drawable.bg_button_primary)
        }
    }

    private fun showConfirmationDialog() {
        val returnOdo = etReturnOdometer.text.toString().toIntOrNull() ?: (initialOdo + 730)
        if (returnOdo < initialOdo) {
            tvErrorOdometer.visibility = View.VISIBLE
            tvErrorOdometer.text = "Odometer akhir ($returnOdo km) tidak boleh lebih rendah dari awal ($initialOdo km)"
            etReturnOdometer.requestFocus()
            Toast.makeText(this, "Validasi gagal: Odometer akhir tidak boleh lebih rendah dari odometer awal!", Toast.LENGTH_SHORT).show()
            return
        }
        val v = activeVehicle!!
        val isGoingToWorkshop = totalEstHours > 0

        val title = if (isGoingToWorkshop) "Konfirmasi Masuk Bengkel Internal" else "Konfirmasi Lulus Inspeksi"
        val message = if (isGoingToWorkshop) {
            "Unit ${v.name} (${v.plateNumber}) akan dimasukkan ke antrean bengkel internal dengan durasi estimasi $totalEstHours Jam.\n\n" +
            "Odometer Akhir: ${currencyFormat.format(returnOdo)} km\n" +
            "Total Estimasi Biaya/Klaim: Rp ${currencyFormat.format(totalEstCost)}\n\n" +
            "Jumlah mobil di kartu BENGKEL pada Beranda akan bertambah secara real-time."
        } else {
            "Unit ${v.name} (${v.plateNumber}) telah dinyatakan bebas cacat dan siap langsung disewakan kembali.\n\n" +
            "Odometer Akhir: ${currencyFormat.format(returnOdo)} km\n" +
            "Status Beranda akan berubah menjadi SIAP SEWA."
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(if (isGoingToWorkshop) R.drawable.ic_build_circle else R.drawable.ic_verified)
            .setPositiveButton("Kirim & Sinkronisasi") { _, _ ->
                val newStatus = if (isGoingToWorkshop) VehicleStatus.IN_WORKSHOP else VehicleStatus.READY
                val targetTime = if (isGoingToWorkshop) "Besok 14:00 WIB (Stall #03)" else "-"

                // Update vehicle state in SharedPreferences (Pertemuan 7)
                prefs.updateVehicleStatus(
                    id = v.id,
                    newStatus = newStatus,
                    odometer = returnOdo,
                    workshopHours = totalEstHours,
                    targetTime = targetTime
                )

                Toast.makeText(this, "Status armada berhasil diperbarui ke SharedPreferences!", Toast.LENGTH_LONG).show()

                // Navigate back to Beranda (Screen 1) with bi-directional synchronization!
                val intent = Intent(this, BerandaFleetActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun shareClaimReport() {
        val report = """
            *LAPORAN INSPEKSI PENGEMBALIAN - AUTOFLEET DIRECT*
            Unit: ${activeVehicle?.name} (${activeVehicle?.plateNumber})
            Odometer Awal: ${currencyFormat.format(initialOdo)} km
            Odometer Akhir: ${etReturnOdometer.text} km
            Trip Perjalanan: ${tvAutoTripCalculated.text}
            
            *Rincian Temuan & Estimasi Bengkel:*
            ${tvClaimItemsSummary.text}
            Estimasi Durasi Pengerjaan: $totalEstHours Jam
            Total Estimasi Biaya / Klaim: Rp ${currencyFormat.format(totalEstCost)}
            Petugas Inspeksi: ${prefs.staffName} (ID: ${prefs.staffId})
            
            Status: ${tvWorkshopAutoStatusTitle.text}
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Bagikan Laporan Klaim & Tagihan"))
    }
}

