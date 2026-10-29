package id.ac.stmi.vehiclerent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.ac.stmi.vehiclerent.model.WorkshopTicketModel
import id.ac.stmi.vehiclerent.util.BottomNavHelper
import id.ac.stmi.vehiclerent.util.MotionUtils
import id.ac.stmi.vehiclerent.util.ProfileDialogHelper
import id.ac.stmi.vehiclerent.util.VehicleRentPrefs
import java.text.NumberFormat
import java.util.Locale

class StallBengkelActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    private lateinit var tvStallSummaryCount: TextView
    private lateinit var tvTotalStallHours: TextView
    private lateinit var tvTotalStallCost: TextView
    private lateinit var llTicketsContainer: LinearLayout

    private var previousHours: Double = 0.0
    private var previousCost: Long = 0L

    private var selectedTicket: WorkshopTicketModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stall_bengkel)

        initViews()
        renderRoleBanner()
        renderTickets()
    }

    override fun onResume() {
        super.onResume()
        ProfileDialogHelper.setupRoleBanner(this) {
            renderTickets()
            BottomNavHelper.setup(this, BottomNavHelper.NavTab.ADMIN_STALL)
        }
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.ADMIN_STALL)
        renderTickets()
    }

    private fun initViews() {
        findViewById<FrameLayout>(R.id.btnToolbarProfile).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this) {
                renderRoleBanner()
                renderTickets()
            }
        }

        findViewById<ImageButton>(R.id.btnToolbarMore).setOnClickListener { view ->
            showOptionsMenuPopup(view)
        }

        findViewById<TextView>(R.id.btnQuickSwitchRole).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this) {
                renderRoleBanner()
                renderTickets()
            }
        }

        tvStallSummaryCount = findViewById(R.id.tvStallSummaryCount)
        tvTotalStallHours = findViewById(R.id.tvTotalStallHours)
        tvTotalStallCost = findViewById(R.id.tvTotalStallCost)
        llTicketsContainer = findViewById(R.id.llTicketsContainer)

        BottomNavHelper.setup(this, BottomNavHelper.NavTab.ADMIN_STALL)
    }

    private fun renderRoleBanner() {
        val bannerContainer = findViewById<LinearLayout>(R.id.llRoleBannerContainer) ?: return
        val ivIcon = findViewById<ImageView>(R.id.ivRoleBannerIcon)
        val tvTitle = findViewById<TextView>(R.id.tvRoleBannerTitle)
        val btnSwitch = findViewById<TextView>(R.id.btnQuickSwitchRole)

        if (prefs.isWorkshopAdmin) {
            bannerContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.workshop_amber_surface))
            ivIcon?.setColorFilter(ContextCompat.getColor(this, R.color.workshop_amber_dark))
            tvTitle?.text = "MODE PEMANGKU USAHA • ${prefs.activeUserName}"
            tvTitle?.setTextColor(ContextCompat.getColor(this, R.color.workshop_amber_dark))
            btnSwitch?.setBackgroundResource(R.drawable.bg_badge_workshop)
            btnSwitch?.setTextColor(ContextCompat.getColor(this, R.color.status_workshop_text))
        } else {
            bannerContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_navy_light))
            ivIcon?.setColorFilter(ContextCompat.getColor(this, R.color.primary_navy))
            tvTitle?.text = "MODE PELANGGAN • ${prefs.activeUserName}"
            tvTitle?.setTextColor(ContextCompat.getColor(this, R.color.primary_navy))
            btnSwitch?.setBackgroundResource(R.drawable.bg_badge_ready)
            btnSwitch?.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))
        }
    }

    private fun renderTickets() {
        llTicketsContainer.removeAllViews()

        val tickets = prefs.getWorkshopTickets()
        tvStallSummaryCount.text = "${tickets.size} Unit Servis"

        val totalHours = tickets.sumOf { it.estimatedHours }
        val totalCost = tickets.sumOf { it.totalCost }

        MotionUtils.animateDoubleRolling(tvTotalStallHours, previousHours, totalHours, suffix = " Jam")
        MotionUtils.animateNumberRolling(tvTotalStallCost, previousCost, totalCost)
        previousHours = totalHours
        previousCost = totalCost

        if (tickets.isEmpty()) {
            val emptyCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setBackgroundResource(R.drawable.bg_card_surface)
                setPadding(32, 48, 32, 48)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 16, 0, 16) }
                layoutParams = lp
            }

            val icon = ImageView(this).apply {
                setImageResource(R.drawable.ic_verified)
                setColorFilter(ContextCompat.getColor(context, R.color.status_ready_text))
                layoutParams = LinearLayout.LayoutParams(64, 64)
            }

            val title = TextView(this).apply {
                text = "Semua Unit Lulus QC (Stall Kosong)"
                setTextAppearance(R.style.TextAppearance_HeadingMD)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 12, 0, 4)
            }

            val sub = TextView(this).apply {
                text = "Tidak ada mobil dalam antrean bengkel internal. Semua armada siap melayani sewa publik."
                setTextAppearance(R.style.TextAppearance_BodySM)
                gravity = android.view.Gravity.CENTER
            }

            emptyCard.addView(icon)
            emptyCard.addView(title)
            emptyCard.addView(sub)
            llTicketsContainer.addView(emptyCard)
            return
        }

        val viewsToAnimate = mutableListOf<View>()
        for (ticket in tickets) {
            val view = layoutInflater.inflate(R.layout.item_stall_ticket, llTicketsContainer, false)

            view.findViewById<TextView>(R.id.tvTicketStallId).text = ticket.stallId
            view.findViewById<TextView>(R.id.tvTicketTargetTime).text = ticket.targetReadyTime
            view.findViewById<TextView>(R.id.tvTicketDtcBadge).text = ticket.dtcCode
            view.findViewById<TextView>(R.id.tvTicketVehicleName).text = ticket.vehicleName
            view.findViewById<TextView>(R.id.tvTicketPlateNumber).text = ticket.plateNumber
            view.findViewById<TextView>(R.id.tvTicketMechanic).text =
                "Teknisi: ${ticket.mechanicName} • Odometer: ${currencyFormat.format(ticket.entryOdometer)} km"
            view.findViewById<TextView>(R.id.tvTicketComponentIssue).text = ticket.componentIssue
            view.findViewById<TextView>(R.id.tvTicketHours).text =
                String.format(Locale.US, "DURASI: %.1f JAM", ticket.estimatedHours)
            view.findViewById<TextView>(R.id.tvTicketCost).text = "Rp ${currencyFormat.format(ticket.totalCost)}"

            val btnQuickRelease = view.findViewById<Button>(R.id.btnQuickRelease)
            MotionUtils.applyTactilePressFeedback(btnQuickRelease)
            btnQuickRelease.setOnClickListener {
                releaseVehicleToCatalog(ticket)
            }

            registerForContextMenu(view)
            view.setOnLongClickListener {
                selectedTicket = ticket
                showTicketContextMenu(it, ticket)
                true
            }

            llTicketsContainer.addView(view)
            viewsToAnimate.add(view)
        }
        MotionUtils.staggeredCascade(viewsToAnimate)
    }

    private fun showTicketContextMenu(anchor: View, ticket: WorkshopTicketModel) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "1. Selesai Servis & Lulus QC (Kembalikan ke Katalog Sewa)")
        popup.menu.add(0, 2, 1, "2. Hubungi Mekanik / Supplier Suku Cadang (Call)")
        popup.menu.add(0, 3, 2, "3. Hapus / Batalkan Tiket Bengkel")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    releaseVehicleToCatalog(ticket)
                    true
                }
                2 -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:081298765432")
                    }
                    startActivity(intent)
                    true
                }
                3 -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Hapus Tiket Servis?")
                        .setMessage("Hapus tiket servis untuk unit ${ticket.vehicleName} (${ticket.plateNumber})?")
                        .setPositiveButton("Hapus") { _, _ ->
                            prefs.removeWorkshopTicket(ticket.ticketId)
                            renderTickets()
                            Toast.makeText(this, "Tiket berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun releaseVehicleToCatalog(ticket: WorkshopTicketModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Lulus QC & Rilis Unit")
            .setMessage("Unit ${ticket.vehicleName} (${ticket.plateNumber}) telah selesai pengerjaan ${ticket.componentIssue}.\n\nStatus akan dikembalikan ke SIAP SEWA (READY) dan langsung dapat dipesan oleh pelanggan di katalog!")
            .setIcon(R.drawable.ic_check_circle)
            .setPositiveButton("Sahkan & Buka Sewa") { _, _ ->
                val releasedCar = prefs.releaseTicketAndMarkReady(ticket.ticketId, ticket.entryOdometer)
                Toast.makeText(
                    this,
                    "Sukses: ${ticket.plateNumber} sekarang SIAP SEWA di Mode Penyewa!",
                    Toast.LENGTH_LONG
                ).show()
                renderTickets()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showOptionsMenuPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "Ganti Peran (Role Switcher)")
        popup.menu.add(0, 2, 1, "Kamus Dataset OBD-II")
        popup.menu.add(0, 3, 2, "Reset Data SharedPreferences")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    ProfileDialogHelper.showProfileDialog(this) {
                        renderRoleBanner()
                        renderTickets()
                    }
                    true
                }
                2 -> {
                    startActivity(Intent(this, ObdDiagnosticActivity::class.java))
                    true
                }
                3 -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Reset Data?")
                        .setMessage("Kembalikan seluruh antrean bengkel dan preferensi ke setelan awal?")
                        .setPositiveButton("Reset") { _, _ ->
                            prefs.clearAndResetAll()
                            renderTickets()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}

