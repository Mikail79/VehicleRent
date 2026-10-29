package id.ac.stmi.vehiclerent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
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

class InspeksiPickUpActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private lateinit var reservation: ReservationModel

    private lateinit var tvPickupVehicleInfo: TextView
    private lateinit var tvPhotoProgressStatus: TextView

    private lateinit var slotRightSidePhoto: LinearLayout
    private lateinit var llRightSidePlaceholder: LinearLayout
    private lateinit var ivRightSidePreview: ImageView
    private lateinit var ivRightSideCheck: ImageView
    private lateinit var tvRightSideStatus: TextView
    private lateinit var tvErrorSlotPhoto: TextView

    private lateinit var etInitialOdometer: EditText
    private lateinit var rgInitialFuel: RadioGroup
    private lateinit var pbFuelGauge: ProgressBar

    private lateinit var tvKitChecklistCount: TextView
    private lateinit var cbKitStnk: CheckBox
    private lateinit var cbKitSpareTire: CheckBox
    private lateinit var cbKitEmergencyKit: CheckBox
    private lateinit var cbKitSmartBundle: CheckBox

    private lateinit var tvSignatureStaffName: TextView
    private lateinit var tvSignatureRenterName: TextView
    private lateinit var btnSubmitHandover: Button

    private var isRightPhotoTaken = false
    private var selectedFuelLevel = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspeksi_pickup)

        loadReservationData()
        initViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        ProfileDialogHelper.setupRoleBanner(this) {
            BottomNavHelper.setup(this, BottomNavHelper.NavTab.RENTER_PICKUP)
        }
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.RENTER_PICKUP)
    }

    private fun loadReservationData() {
        val extra = intent.getSerializableExtra(DetailReservasiActivity.EXTRA_RESERVATION) as? ReservationModel
        if (extra != null) {
            reservation = extra
        } else {
            val defaultCar = prefs.getVehicleById("AF-01") ?: VehicleModel(
                id = "AF-01",
                name = "Toyota Innova Zenix 2.0 G CVT",
                category = "MPV Keluarga (Zenix / Xpander)",
                plateNumber = "B 1842 KZR",
                specs = "Automatic • 7 Penumpang • Bensin",
                stagingBay = "Bay B-04",
                lastOdometer = 24120,
                fuelPercent = 100,
                baseRatePerDay = 650000L,
                status = VehicleStatus.READY
            )
            reservation = ReservationModel(
                bookingCode = "#AF-2026-089",
                vehicle = defaultCar,
                renterName = prefs.draftRenterName,
                renterPhone = prefs.draftWa,
                renterIdAndSim = prefs.draftSim,
                durationDays = 3,
                pickupDate = "24 Okt 2026",
                isWithDriver = false,
                isPackageBundling = true,
                hasFamilyTouring = true,
                hasDashcamGps = true,
                hasCoolerBox = false,
                paymentMethod = "Transfer Virtual Account (BCA)",
                baseRentTotal = 1950000L,
                driverTotal = 0L,
                bundleTotal = 150000L,
                subtotalRent = 2100000L,
                depositAmount = 500000L,
                totalPayment = 2600000L,
                qcPassStaff = prefs.staffName,
                initialOdometer = 24120,
                initialFuel = 100
            )
        }
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<FrameLayout>(R.id.btnToolbarProfile).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this) {
                tvSignatureStaffName.text = "ID: ${prefs.staffId} (${prefs.staffName})"
            }
        }

        tvPickupVehicleInfo = findViewById(R.id.tvPickupVehicleInfo)
        tvPhotoProgressStatus = findViewById(R.id.tvPhotoProgressStatus)

        slotRightSidePhoto = findViewById(R.id.slotRightSidePhoto)
        llRightSidePlaceholder = findViewById(R.id.llRightSidePlaceholder)
        ivRightSidePreview = findViewById(R.id.ivRightSidePreview)
        ivRightSideCheck = findViewById(R.id.ivRightSideCheck)
        tvRightSideStatus = findViewById(R.id.tvRightSideStatus)

        etInitialOdometer = findViewById(R.id.etInitialOdometer)
        rgInitialFuel = findViewById(R.id.rgInitialFuel)
        pbFuelGauge = findViewById(R.id.pbFuelGauge)

        tvKitChecklistCount = findViewById(R.id.tvKitChecklistCount)
        cbKitStnk = findViewById(R.id.cbKitStnk)
        cbKitSpareTire = findViewById(R.id.cbKitSpareTire)
        cbKitEmergencyKit = findViewById(R.id.cbKitEmergencyKit)
        cbKitSmartBundle = findViewById(R.id.cbKitSmartBundle)

        tvSignatureStaffName = findViewById(R.id.tvSignatureStaffName)
        tvSignatureRenterName = findViewById(R.id.tvSignatureRenterName)
        tvErrorSlotPhoto = findViewById(R.id.tvErrorSlotPhoto)
        btnSubmitHandover = findViewById(R.id.btnSubmitHandover)

        // Setup Bottom Nav and Tactile Feedback
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.INSPEKSI)
        MotionUtils.applyTactilePressFeedback(slotRightSidePhoto)
        MotionUtils.applyTactilePressFeedback(btnSubmitHandover)

        // Bind data
        val v = reservation.vehicle
        tvPickupVehicleInfo.text = "${v.plateNumber} • ${v.name}"
        etInitialOdometer.setText(v.lastOdometer.toString())

        tvSignatureStaffName.text = "ID: ${prefs.staffId} (${prefs.staffName})"
        tvSignatureRenterName.text = reservation.renterName
    }

    private fun setupListeners() {
        // Interactive Right-side photo
        slotRightSidePhoto.setOnClickListener {
            if (!isRightPhotoTaken) {
                isRightPhotoTaken = true
                llRightSidePlaceholder.visibility = View.GONE
                ivRightSidePreview.visibility = View.VISIBLE
                ivRightSideCheck.visibility = View.VISIBLE
                tvErrorSlotPhoto.visibility = View.GONE

                tvRightSideStatus.text = "FOTO TERVERIFIKASI"
                tvRightSideStatus.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))
                tvPhotoProgressStatus.text = "4 / 4 Selesai"
                tvPhotoProgressStatus.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))

                Toast.makeText(this, "Foto sisi kanan berhasil diverifikasi (4/4 Selesai)!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Foto sisi kanan sudah terverifikasi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Fuel level selector
        rgInitialFuel.setOnCheckedChangeListener { _, checkedId ->
            selectedFuelLevel = when (checkedId) {
                R.id.rbFuel25 -> 25
                R.id.rbFuel50 -> 50
                R.id.rbFuel75 -> 75
                else -> 100
            }
            pbFuelGauge.progress = selectedFuelLevel
        }

        // Checklist listener
        val kitWatcher = View.OnClickListener {
            var count = 0
            if (cbKitStnk.isChecked) count++
            if (cbKitSpareTire.isChecked) count++
            if (cbKitEmergencyKit.isChecked) count++
            if (cbKitSmartBundle.isChecked) count++

            tvKitChecklistCount.text = "$count/4 SIAP"
            if (count == 4) {
                tvKitChecklistCount.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))
            } else {
                tvKitChecklistCount.setTextColor(ContextCompat.getColor(this, R.color.status_workshop_text))
            }
        }
        cbKitStnk.setOnClickListener(kitWatcher)
        cbKitSpareTire.setOnClickListener(kitWatcher)
        cbKitEmergencyKit.setOnClickListener(kitWatcher)
        cbKitSmartBundle.setOnClickListener(kitWatcher)

        // Submit Button
        btnSubmitHandover.setOnClickListener {
            submitInspection()
        }
    }

    private fun submitInspection() {
        if (!isRightPhotoTaken) {
            tvErrorSlotPhoto.visibility = View.VISIBLE
            slotRightSidePhoto.animate().translationX(-12f).setDuration(50).withEndAction {
                slotRightSidePhoto.animate().translationX(12f).setDuration(50).withEndAction {
                    slotRightSidePhoto.animate().translationX(0f).setDuration(50).start()
                }.start()
            }.start()

            MaterialAlertDialogBuilder(this)
                .setTitle("Foto Fisik Belum Lengkap")
                .setMessage("Silakan klik kotak 'Sisi Kanan' untuk mengambil foto kendaraan sebelum serah-terima.")
                .setIcon(R.drawable.ic_photo_camera)
                .setPositiveButton("Ambil Sekarang") { _, _ ->
                    slotRightSidePhoto.performClick()
                }
                .setNegativeButton("Batal", null)
                .show()
            return
        }

        val odoStr = etInitialOdometer.text.toString().trim()
        val odo = odoStr.toIntOrNull() ?: reservation.vehicle.lastOdometer

        // Save initial telemetry to SharedPreferences
        prefs.initialOdometer = odo
        prefs.initialFuel = selectedFuelLevel

        // Update Vehicle Status to ON_RENT in SharedPreferences
        prefs.updateVehicleStatus(
            id = reservation.vehicle.id,
            newStatus = VehicleStatus.ON_RENT,
            odometer = odo,
            fuel = selectedFuelLevel
        )

        reservation.initialOdometer = odo
        reservation.initialFuel = selectedFuelLevel
        reservation.vehicle.status = VehicleStatus.ON_RENT

        MaterialAlertDialogBuilder(this)
            .setTitle("Serah-Terima Berhasil Disahkan!")
            .setIcon(R.drawable.ic_verified)
            .setMessage(
                "Unit: ${reservation.vehicle.name} (${reservation.vehicle.plateNumber})\n" +
                "Penyewa: ${reservation.renterName}\n" +
                "Odometer Awal: $odo km\n" +
                "Bahan Bakar Awal: $selectedFuelLevel%\n" +
                "Petugas Fleet: ${prefs.staffName}\n\n" +
                "Mobil resmi berstatus SEDANG DISEWA. Lanjut ke simulasi pengembalian unit?"
            )
            .setPositiveButton("Lanjut ke Inspeksi Return") { _, _ ->
                val intent = Intent(this, InspeksiReturnActivity::class.java).apply {
                    putExtra(DetailReservasiActivity.EXTRA_RESERVATION, reservation)
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Kembali ke Beranda") { _, _ ->
                val intent = Intent(this, BerandaFleetActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .show()
    }
}

