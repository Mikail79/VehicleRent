package id.ac.stmi.vehiclerent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import id.ac.stmi.vehiclerent.model.ReservationModel
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import id.ac.stmi.vehiclerent.util.BottomNavHelper
import id.ac.stmi.vehiclerent.util.MotionUtils
import id.ac.stmi.vehiclerent.util.ProfileDialogHelper
import id.ac.stmi.vehiclerent.util.VehicleRentPrefs
import java.text.NumberFormat
import java.util.Locale

class DetailReservasiActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    private lateinit var reservation: ReservationModel

    private lateinit var tvSubtitleBookingCode: TextView
    private lateinit var tvPoolLocation: TextView
    private lateinit var tvQcPassStaff: TextView

    private lateinit var tvTicketBookingCode: TextView
    private lateinit var tvDetailVehicleName: TextView
    private lateinit var tvDetailVehicleSpecs: TextView
    private lateinit var tvDetailPlateNumber: TextView

    private lateinit var tvAvatarInitials: TextView
    private lateinit var tvDetailRenterName: TextView
    private lateinit var tvDetailRenterPhone: TextView
    private lateinit var tvDetailSimNumber: TextView

    private lateinit var tvDetailPackageTitle: TextView
    private lateinit var tvDetailPackageBadges: TextView

    private lateinit var tvDetailPickupDate: TextView
    private lateinit var tvDetailDropoffDate: TextView
    private lateinit var tvDetailDropoffDuration: TextView

    private lateinit var tvReceiptCarRental: TextView
    private lateinit var tvReceiptCarRentalCost: TextView
    private lateinit var llReceiptDriverRow: LinearLayout
    private lateinit var tvReceiptDriverCost: TextView
    private lateinit var llReceiptBundleRow: LinearLayout
    private lateinit var tvReceiptBundleCost: TextView
    private lateinit var tvReceiptPaymentStatus: TextView
    private lateinit var tvReceiptTotalAmount: TextView

    private lateinit var btnStartPickupInspection: Button
    private lateinit var btnToggleIntentSheet: Button
    private lateinit var llIntentSheet: LinearLayout

    private lateinit var btnIntentWhatsApp: LinearLayout
    private lateinit var btnIntentDialer: LinearLayout
    private lateinit var tvIntentDialerPhone: TextView
    private lateinit var btnIntentMaps: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_reservasi)

        loadReservationData()
        initViews()
        bindData()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        ProfileDialogHelper.setupRoleBanner(this) {
            BottomNavHelper.setup(this, BottomNavHelper.NavTab.RENTER_RESERVASI)
        }
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.RENTER_RESERVASI)
    }

    private fun loadReservationData() {
        val extra = intent.getSerializableExtra(FormPemesananActivity.EXTRA_RESERVATION) as? ReservationModel
        if (extra != null) {
            reservation = extra
        } else {
            // Default demo reservation matching Stitch wireframe
            val defaultCar = prefs.getVehicleById("AF-01") ?: VehicleModel(
                id = "AF-01",
                name = "Toyota Innova Zenix 2.0 G CVT",
                category = "MPV Keluarga (Zenix / Xpander)",
                plateNumber = "B 1842 KZR",
                specs = "MPV Premium • Dark Platinum Gray • Bensin",
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
                tvQcPassStaff.text = "QC Pass: 24/10 07:30 oleh Mekanik ${prefs.staffName}"
            }
        }

        findViewById<ImageButton>(R.id.btnToolbarMore).setOnClickListener { view ->
            showToolbarMenu(view)
        }

        tvSubtitleBookingCode = findViewById(R.id.tvSubtitleBookingCode)
        tvPoolLocation = findViewById(R.id.tvPoolLocation)
        tvQcPassStaff = findViewById(R.id.tvQcPassStaff)

        tvTicketBookingCode = findViewById(R.id.tvTicketBookingCode)
        tvDetailVehicleName = findViewById(R.id.tvDetailVehicleName)
        tvDetailVehicleSpecs = findViewById(R.id.tvDetailVehicleSpecs)
        tvDetailPlateNumber = findViewById(R.id.tvDetailPlateNumber)

        tvAvatarInitials = findViewById(R.id.tvAvatarInitials)
        tvDetailRenterName = findViewById(R.id.tvDetailRenterName)
        tvDetailRenterPhone = findViewById(R.id.tvDetailRenterPhone)
        tvDetailSimNumber = findViewById(R.id.tvDetailSimNumber)

        tvDetailPackageTitle = findViewById(R.id.tvDetailPackageTitle)
        tvDetailPackageBadges = findViewById(R.id.tvDetailPackageBadges)

        tvDetailPickupDate = findViewById(R.id.tvDetailPickupDate)
        tvDetailDropoffDate = findViewById(R.id.tvDetailDropoffDate)
        tvDetailDropoffDuration = findViewById(R.id.tvDetailDropoffDuration)

        tvReceiptCarRental = findViewById(R.id.tvReceiptCarRental)
        tvReceiptCarRentalCost = findViewById(R.id.tvReceiptCarRentalCost)
        llReceiptDriverRow = findViewById(R.id.llReceiptDriverRow)
        tvReceiptDriverCost = findViewById(R.id.tvReceiptDriverCost)
        llReceiptBundleRow = findViewById(R.id.llReceiptBundleRow)
        tvReceiptBundleCost = findViewById(R.id.tvReceiptBundleCost)
        tvReceiptPaymentStatus = findViewById(R.id.tvReceiptPaymentStatus)
        tvReceiptTotalAmount = findViewById(R.id.tvReceiptTotalAmount)

        btnStartPickupInspection = findViewById(R.id.btnStartPickupInspection)
        btnToggleIntentSheet = findViewById(R.id.btnToggleIntentSheet)
        llIntentSheet = findViewById(R.id.llIntentSheet)

        btnIntentWhatsApp = findViewById(R.id.btnIntentWhatsApp)
        btnIntentDialer = findViewById(R.id.btnIntentDialer)
        tvIntentDialerPhone = findViewById(R.id.tvIntentDialerPhone)
        btnIntentMaps = findViewById(R.id.btnIntentMaps)

        // Setup Bottom Nav and Tactile Feedback
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.RESERVASI)
        MotionUtils.applyTactilePressFeedback(btnStartPickupInspection)
        MotionUtils.applyTactilePressFeedback(btnToggleIntentSheet)
        MotionUtils.applyTactilePressFeedback(btnIntentWhatsApp)
        MotionUtils.applyTactilePressFeedback(btnIntentDialer)
        MotionUtils.applyTactilePressFeedback(btnIntentMaps)
    }

    private fun bindData() {
        val code = reservation.bookingCode
        tvSubtitleBookingCode.text = code
        tvTicketBookingCode.text = code.replace("#", "")

        tvPoolLocation.text = "Pool Rental Soekarno-Hatta (${reservation.vehicle.stagingBay})"
        tvQcPassStaff.text = "QC Pass: 24/10 07:30 oleh Mekanik ${prefs.staffName}"

        val v = reservation.vehicle
        tvDetailVehicleName.text = v.name
        tvDetailVehicleSpecs.text = v.specs
        tvDetailPlateNumber.text = v.plateNumber

        // Renter Avatar Initials
        val words = reservation.renterName.split(" ").filter { it.isNotEmpty() }
        val initials = if (words.size >= 2) {
            "${words[0][0]}${words[1][0]}"
        } else if (words.isNotEmpty() && words[0].isNotEmpty()) {
            words[0].take(2)
        } else {
            "BS"
        }
        tvAvatarInitials.text = initials.uppercase()

        tvDetailRenterName.text = reservation.renterName
        tvDetailRenterPhone.text = "${reservation.renterPhone} • Verified ID"
        tvDetailSimNumber.text = reservation.renterIdAndSim

        // Package
        val packageType = if (reservation.isWithDriver) "Dengan Sopir" else "Lepas Kunci"
        val bundleType = if (reservation.hasFamilyTouring) "+ Smart Bundle Family Touring" else "+ Standar Armada"
        tvDetailPackageTitle.text = "$packageType $bundleType"

        val badges = mutableListOf<String>()
        if (reservation.hasFamilyTouring) badges.add("Roof Box 400L")
        if (reservation.hasFamilyTouring) badges.add("Child Seat Isofix")
        if (reservation.hasFamilyTouring) badges.add("E-Toll Card Fleet")
        if (reservation.hasDashcamGps) badges.add("Dashcam Dual-Cam 2K")
        if (reservation.hasCoolerBox) badges.add("Cooler Box 12V")
        if (badges.isEmpty()) badges.add("Kelengkapan Standar Pabrik")
        tvDetailPackageBadges.text = badges.joinToString(" • ")

        // Dates
        tvDetailPickupDate.text = reservation.pickupDate
        tvDetailDropoffDate.text = "27 Okt 2026"
        tvDetailDropoffDuration.text = "09:00 WIB (${reservation.durationDays} Hari)"

        // Cost Breakdown
        tvReceiptCarRental.text = "Sewa Mobil (${reservation.durationDays} x Rp ${currencyFormat.format(v.baseRatePerDay)})"
        tvReceiptCarRentalCost.text = "Rp ${currencyFormat.format(reservation.baseRentTotal)}"

        if (reservation.driverTotal > 0) {
            llReceiptDriverRow.visibility = View.VISIBLE
            tvReceiptDriverCost.text = "Rp ${currencyFormat.format(reservation.driverTotal)}"
        } else {
            llReceiptDriverRow.visibility = View.GONE
        }

        if (reservation.bundleTotal > 0) {
            llReceiptBundleRow.visibility = View.VISIBLE
            tvReceiptBundleCost.text = "Rp ${currencyFormat.format(reservation.bundleTotal)}"
        } else {
            llReceiptBundleRow.visibility = View.GONE
        }

        tvReceiptPaymentStatus.text = "Terkonfirmasi via ${reservation.paymentMethod}"
        tvReceiptTotalAmount.text = "Rp ${currencyFormat.format(reservation.totalPayment)}"

        tvIntentDialerPhone.text = "Intent: ACTION_DIAL (${reservation.renterPhone})"
    }

    private fun setupListeners() {
        btnStartPickupInspection.setOnClickListener {
            val intent = Intent(this, InspeksiPickUpActivity::class.java).apply {
                putExtra(EXTRA_RESERVATION, reservation)
            }
            startActivity(intent)
        }

        btnToggleIntentSheet.setOnClickListener { view ->
            // Toggle sheet and open PopupMenu (Pertemuan 6)
            if (llIntentSheet.visibility == View.VISIBLE) {
                llIntentSheet.visibility = View.GONE
            } else {
                llIntentSheet.visibility = View.VISIBLE
            }
            showQuickActionsPopup(view)
        }

        // Implicit Intent 1: WhatsApp (Pertemuan 3)
        btnIntentWhatsApp.setOnClickListener {
            launchWhatsAppIntent()
        }

        // Implicit Intent 2: Dialer (Pertemuan 3)
        btnIntentDialer.setOnClickListener {
            launchDialerIntent()
        }

        // Implicit Intent 3: Maps (Pertemuan 3)
        btnIntentMaps.setOnClickListener {
            launchMapsIntent()
        }
    }

    private fun launchWhatsAppIntent() {
        val receiptText = """
            *BUKTI RESERVASI AUTOFLEET DIRECT*
            Kode Booking: ${reservation.bookingCode}
            Unit: ${reservation.vehicle.name} (${reservation.vehicle.plateNumber})
            Penyewa: ${reservation.renterName}
            Durasi: ${reservation.durationDays} Hari (Mulai: ${reservation.pickupDate})
            Total Bayar: Rp ${currencyFormat.format(reservation.totalPayment)}
            Status: Terkonfirmasi • QC Pass: ${prefs.staffName}
            
            Silakan tunjukkan pesan ini saat serah terima di pool.
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, receiptText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Kirim Bukti via WhatsApp / Pesan")
        try {
            startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak ada aplikasi untuk membagikan bukti", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchDialerIntent() {
        val cleanPhone = reservation.renterPhone.replace("[^0-9+]".toRegex(), "")
        val dialUri = Uri.parse("tel:$cleanPhone")
        val dialIntent = Intent(Intent.ACTION_DIAL, dialUri)
        try {
            startActivity(dialIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka dialer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchMapsIntent() {
        val poolGeoUri = Uri.parse("geo:0,0?q=Pool+Rental+Soekarno+Hatta")
        val mapIntent = Intent(Intent.ACTION_VIEW, poolGeoUri)
        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka aplikasi Maps", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showQuickActionsPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "1. Kirim Bukti via WhatsApp")
        popup.menu.add(0, 2, 1, "2. Telepon Penyewa (Dialer)")
        popup.menu.add(0, 3, 2, "3. Buka Lokasi Pool di Maps")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    launchWhatsAppIntent()
                    true
                }
                2 -> {
                    launchDialerIntent()
                    true
                }
                3 -> {
                    launchMapsIntent()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showToolbarMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "Profil Petugas")
        popup.menu.add(0, 2, 1, "Kembali ke Beranda")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    ProfileDialogHelper.showProfileDialog(this)
                    true
                }
                2 -> {
                    val intent = Intent(this, BerandaFleetActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
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

