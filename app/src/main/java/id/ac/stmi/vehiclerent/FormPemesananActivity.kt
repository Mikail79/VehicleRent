package id.ac.stmi.vehiclerent

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.ac.stmi.vehiclerent.model.ReservationModel
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import id.ac.stmi.vehiclerent.util.BottomNavHelper
import id.ac.stmi.vehiclerent.util.MotionUtils
import id.ac.stmi.vehiclerent.util.ProfileDialogHelper
import id.ac.stmi.vehiclerent.util.VehicleRentPrefs
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormPemesananActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    private var selectedVehicle: VehicleModel? = null
    private var isWithDriver: Boolean = false
    private var isBundlingMode: Boolean = true
    private var hasFamilyTouring: Boolean = true
    private var hasDashcamGps: Boolean = true
    private var hasCoolerBox: Boolean = false

    private var durationDays: Int = 3
    private var selectedDateCalendar: Calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    private lateinit var tvOrderVehicleName: TextView
    private lateinit var tvOrderPlateNumber: TextView
    private lateinit var tvOrderBundleSummary: TextView
    private lateinit var tvOrderDailyRate: TextView

    private lateinit var etRenterName: EditText
    private lateinit var tvErrorRenterName: TextView
    private lateinit var etRenterPhone: EditText
    private lateinit var etRenterId: EditText
    private lateinit var tvErrorRenterId: TextView

    private lateinit var btnDecrement: ImageButton
    private lateinit var btnIncrement: ImageButton
    private lateinit var tvDurationDisplay: TextView
    private lateinit var btnPickDate: LinearLayout
    private lateinit var tvSelectedDate: TextView
    private lateinit var spinnerPaymentMethod: Spinner

    private lateinit var tvLabelCarRental: TextView
    private lateinit var tvCostCarRental: TextView
    private lateinit var llRowDriver: LinearLayout
    private lateinit var tvLabelDriver: TextView
    private lateinit var tvCostDriver: TextView
    private lateinit var llRowBundle: LinearLayout
    private lateinit var tvLabelBundle: TextView
    private lateinit var tvCostBundle: TextView
    private lateinit var tvPaymentMethodSummary: TextView
    private lateinit var tvGrandTotal: TextView
    private lateinit var btnConfirmReservation: Button

    private var previousGrandTotal: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_pemesanan)

        extractIntentExtras()
        initViews()
        loadDraftFromPrefs()
        setupListeners()
        calculateAndRenderCosts()
    }

    override fun onResume() {
        super.onResume()
        ProfileDialogHelper.setupRoleBanner(this) {
            BottomNavHelper.setup(this, BottomNavHelper.NavTab.RENTER_BOOKING)
        }
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.RENTER_BOOKING)
    }

    private fun extractIntentExtras() {
        val extraVehicle = intent.getSerializableExtra(BerandaFleetActivity.EXTRA_VEHICLE) as? VehicleModel
        selectedVehicle = extraVehicle ?: prefs.getFleetList().firstOrNull() ?: VehicleModel(
            id = "AF-01",
            name = "Toyota Kijang Innova Zenix 2.0 G CVT",
            category = "MPV Keluarga (Zenix / Xpander)",
            plateNumber = "B 1842 KZR",
            specs = "Automatic • 7 Penumpang • Bensin",
            stagingBay = "Bay B-04",
            lastOdometer = 24120,
            fuelPercent = 100,
            baseRatePerDay = 650000L,
            status = VehicleStatus.READY
        )

        isWithDriver = intent.getBooleanExtra(BerandaFleetActivity.EXTRA_WITH_DRIVER, false)
        isBundlingMode = intent.getBooleanExtra(BerandaFleetActivity.EXTRA_IS_BUNDLING, true)
        hasFamilyTouring = intent.getBooleanExtra(BerandaFleetActivity.EXTRA_FAMILY_TOURING, true)
        hasDashcamGps = intent.getBooleanExtra(BerandaFleetActivity.EXTRA_DASHCAM_GPS, true)
        hasCoolerBox = intent.getBooleanExtra(BerandaFleetActivity.EXTRA_COOLER_BOX, false)
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<FrameLayout>(R.id.btnToolbarProfile).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this)
        }

        findViewById<ImageButton>(R.id.btnToolbarMore).setOnClickListener { view ->
            showToolbarMenu(view)
        }

        tvOrderVehicleName = findViewById(R.id.tvOrderVehicleName)
        tvOrderPlateNumber = findViewById(R.id.tvOrderPlateNumber)
        tvOrderBundleSummary = findViewById(R.id.tvOrderBundleSummary)
        tvOrderDailyRate = findViewById(R.id.tvOrderDailyRate)

        etRenterName = findViewById(R.id.etRenterName)
        tvErrorRenterName = findViewById(R.id.tvErrorRenterName)
        etRenterPhone = findViewById(R.id.etRenterPhone)
        etRenterId = findViewById(R.id.etRenterId)
        tvErrorRenterId = findViewById(R.id.tvErrorRenterId)

        btnDecrement = findViewById(R.id.btnDecrementDuration)
        btnIncrement = findViewById(R.id.btnIncrementDuration)
        tvDurationDisplay = findViewById(R.id.tvDurationDisplay)
        btnPickDate = findViewById(R.id.btnPickDate)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod)

        tvLabelCarRental = findViewById(R.id.tvLabelCarRental)
        tvCostCarRental = findViewById(R.id.tvCostCarRental)
        llRowDriver = findViewById(R.id.llRowDriver)
        tvLabelDriver = findViewById(R.id.tvLabelDriver)
        tvCostDriver = findViewById(R.id.tvCostDriver)
        llRowBundle = findViewById(R.id.llRowBundle)
        tvLabelBundle = findViewById(R.id.tvLabelBundle)
        tvCostBundle = findViewById(R.id.tvCostBundle)
        tvPaymentMethodSummary = findViewById(R.id.tvPaymentMethodSummary)
        tvGrandTotal = findViewById(R.id.tvGrandTotal)
        btnConfirmReservation = findViewById(R.id.btnConfirmReservation)

        // Setup Bottom Nav and Tactile Feedback
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.BERANDA)
        MotionUtils.applyTactilePressFeedback(btnDecrement)
        MotionUtils.applyTactilePressFeedback(btnIncrement)
        MotionUtils.applyTactilePressFeedback(btnPickDate)
        MotionUtils.applyTactilePressFeedback(btnConfirmReservation)

        val v = selectedVehicle!!
        tvOrderVehicleName.text = v.name
        tvOrderPlateNumber.text = v.plateNumber
        tvOrderDailyRate.text = "Rp ${currencyFormat.format(v.baseRatePerDay)} / hari"

        val bundleNames = mutableListOf<String>()
        if (isWithDriver) bundleNames.add("Dengan Sopir (+Rp 250rb)")
        if (hasFamilyTouring) bundleNames.add("Family Touring")
        if (hasDashcamGps) bundleNames.add("Dashcam GPS")
        if (hasCoolerBox) bundleNames.add("Cooler Box")
        if (bundleNames.isEmpty()) bundleNames.add("Lepas Kunci Standar (Unit Saja)")
        tvOrderBundleSummary.text = bundleNames.joinToString(" • ")

        val paymentMethods = listOf(
            "Bayar di Kantor Cabang (Pay at Store)",
            "Transfer Virtual Account (BCA)",
            "Transfer Virtual Account (Mandiri)"
        )
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods)
        spinnerPaymentMethod.adapter = spAdapter

        selectedDateCalendar.set(2026, Calendar.OCTOBER, 24)
        tvSelectedDate.text = dateFormatter.format(selectedDateCalendar.time)
    }

    private fun loadDraftFromPrefs() {
        etRenterName.setText(prefs.draftRenterName)
        etRenterPhone.setText(prefs.draftWa)
        etRenterId.setText(prefs.draftSim)
    }

    private fun setupListeners() {
        // Auto-save drafts to SharedPreferences
        val autoSaveWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                prefs.draftRenterName = etRenterName.text.toString().trim()
                prefs.draftWa = etRenterPhone.text.toString().trim()
                prefs.draftSim = etRenterId.text.toString().trim()
            }
        }
        etRenterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!s.isNullOrBlank()) tvErrorRenterName.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {
                prefs.draftRenterName = s.toString().trim()
            }
        })

        etRenterPhone.addTextChangedListener(autoSaveWatcher)

        etRenterId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!s.isNullOrBlank()) tvErrorRenterId.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {
                prefs.draftSim = s.toString().trim()
            }
        })

        // Stepper duration
        btnDecrement.setOnClickListener {
            if (durationDays > 1) {
                durationDays--
                tvDurationDisplay.text = "$durationDays Hari"
                MotionUtils.pulseHighlight(tvDurationDisplay)
                calculateAndRenderCosts()
            }
        }

        btnIncrement.setOnClickListener {
            durationDays++
            tvDurationDisplay.text = "$durationDays Hari"
            MotionUtils.pulseHighlight(tvDurationDisplay)
            calculateAndRenderCosts()
        }

        // Date picker
        btnPickDate.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDateCalendar.set(year, month, dayOfMonth)
                    tvSelectedDate.text = dateFormatter.format(selectedDateCalendar.time)
                },
                selectedDateCalendar.get(Calendar.YEAR),
                selectedDateCalendar.get(Calendar.MONTH),
                selectedDateCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        spinnerPaymentMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = spinnerPaymentMethod.selectedItem.toString()
                tvPaymentMethodSummary.text = selected.uppercase()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnConfirmReservation.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun calculateAndRenderCosts() {
        val v = selectedVehicle!!
        val baseRate = v.baseRatePerDay

        val carRentTotal = durationDays * baseRate
        val driverTotal = if (isWithDriver) durationDays * 250000L else 0L

        var dailyBundleRate = 0L
        if (hasFamilyTouring) dailyBundleRate += 150000L
        if (hasCoolerBox) dailyBundleRate += 50000L
        val bundleTotal = durationDays * dailyBundleRate

        val subtotal = carRentTotal + driverTotal + bundleTotal
        val deposit = 500000L
        val grandTotal = subtotal + deposit

        tvLabelCarRental.text = "Total Sewa Unit ($durationDays Hari x Rp ${currencyFormat.format(baseRate)})"
        tvCostCarRental.text = "Rp ${currencyFormat.format(carRentTotal)}"

        if (isWithDriver) {
            llRowDriver.visibility = View.VISIBLE
            tvLabelDriver.text = "Biaya Sopir Profesional ($durationDays Hari x Rp 250.000)"
            tvCostDriver.text = "Rp ${currencyFormat.format(driverTotal)}"
        } else {
            llRowDriver.visibility = View.GONE
        }

        if (bundleTotal > 0L) {
            llRowBundle.visibility = View.VISIBLE
            tvLabelBundle.text = "Smart Fleet Bundle Add-ons ($durationDays Hari)"
            tvCostBundle.text = "Rp ${currencyFormat.format(bundleTotal)}"
        } else {
            llRowBundle.visibility = View.GONE
        }

        // Numeric rolling counter (450ms ease-out)
        MotionUtils.animateNumberRolling(tvGrandTotal, previousGrandTotal, grandTotal)
        previousGrandTotal = grandTotal
    }

    private fun validateAndSubmit() {
        val name = etRenterName.text.toString().trim()
        val phone = etRenterPhone.text.toString().trim()
        val idSim = etRenterId.text.toString().trim()

        var hasError = false

        if (name.isEmpty()) {
            tvErrorRenterName.visibility = View.VISIBLE
            etRenterName.requestFocus()
            hasError = true
        } else {
            tvErrorRenterName.visibility = View.GONE
        }

        if (phone.isEmpty()) {
            etRenterPhone.error = "Nomor WhatsApp tidak boleh kosong"
            if (!hasError) etRenterPhone.requestFocus()
            hasError = true
        }

        if (idSim.isEmpty()) {
            tvErrorRenterId.visibility = View.VISIBLE
            if (!hasError) etRenterId.requestFocus()
            hasError = true
        } else {
            tvErrorRenterId.visibility = View.GONE
        }

        if (hasError) return

        val v = selectedVehicle!!
        val baseRate = v.baseRatePerDay
        val carRentTotal = durationDays * baseRate
        val driverTotal = if (isWithDriver) durationDays * 250000L else 0L

        var dailyBundleRate = 0L
        if (hasFamilyTouring) dailyBundleRate += 150000L
        if (hasCoolerBox) dailyBundleRate += 50000L
        val bundleTotal = durationDays * dailyBundleRate

        val subtotal = carRentTotal + driverTotal + bundleTotal
        val deposit = 500000L
        val grandTotal = subtotal + deposit

        val reservation = ReservationModel(
            bookingCode = "#AF-2026-089",
            vehicle = v,
            renterName = name,
            renterPhone = if (phone.startsWith("+62")) phone else "+62$phone",
            renterIdAndSim = idSim,
            durationDays = durationDays,
            pickupDate = tvSelectedDate.text.toString(),
            isWithDriver = isWithDriver,
            isPackageBundling = isBundlingMode,
            hasFamilyTouring = hasFamilyTouring,
            hasDashcamGps = hasDashcamGps,
            hasCoolerBox = hasCoolerBox,
            paymentMethod = spinnerPaymentMethod.selectedItem.toString(),
            baseRentTotal = carRentTotal,
            driverTotal = driverTotal,
            bundleTotal = bundleTotal,
            subtotalRent = subtotal,
            depositAmount = deposit,
            totalPayment = grandTotal,
            qcPassStaff = prefs.staffName,
            initialOdometer = v.lastOdometer,
            initialFuel = v.fuelPercent
        )

        val intent = Intent(this, DetailReservasiActivity::class.java).apply {
            putExtra(EXTRA_RESERVATION, reservation)
        }
        startActivity(intent)
    }

    private fun showToolbarMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "Profil Petugas")
        popup.menu.add(0, 2, 1, "Bersihkan Form (Reset Draft)")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    ProfileDialogHelper.showProfileDialog(this)
                    true
                }
                2 -> {
                    prefs.draftRenterName = ""
                    prefs.draftWa = ""
                    prefs.draftSim = ""
                    etRenterName.setText("")
                    etRenterPhone.setText("")
                    etRenterId.setText("")
                    Toast.makeText(this, "Draft formulir dibersihkan", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    companion object {
        const val EXTRA_RESERVATION = "EXTRA_RESERVATION"
    }
}

