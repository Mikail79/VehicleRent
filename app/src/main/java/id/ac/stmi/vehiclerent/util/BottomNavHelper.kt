package id.ac.stmi.vehiclerent.util

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import id.ac.stmi.vehiclerent.BerandaFleetActivity
import id.ac.stmi.vehiclerent.DetailReservasiActivity
import id.ac.stmi.vehiclerent.FormPemesananActivity
import id.ac.stmi.vehiclerent.InspeksiPickUpActivity
import id.ac.stmi.vehiclerent.InspeksiReturnActivity
import id.ac.stmi.vehiclerent.ObdDiagnosticActivity
import id.ac.stmi.vehiclerent.R
import id.ac.stmi.vehiclerent.StallBengkelActivity

/**
 * Connects and coordinates the Bottom Navigation Bar dynamically across both roles:
 * - MODE PENYEWA: (1) Katalog & Bundle, (2) Form Booking, (3) Tiket Reservasi, (4) Cek Pick-up.
 * - MODE PEMANGKU USAHA: (1) Command Fleet, (2) Inspeksi Return, (3) Kamus OBD-II, (4) Antrean Stall.
 */
object BottomNavHelper {

    enum class NavTab {
        RENTER_KATALOG,
        RENTER_BOOKING,
        RENTER_RESERVASI,
        RENTER_PICKUP,

        ADMIN_COMMAND,
        ADMIN_RETURN,
        ADMIN_OBD,
        ADMIN_STALL,

        // Backwards compatibility mappings
        BERANDA,
        RESERVASI,
        INSPEKSI,
        BENGKEL
    }

    fun setup(activity: Activity, currentTab: NavTab) {
        val nav1 = activity.findViewById<View>(R.id.navBeranda) ?: return
        val nav2 = activity.findViewById<View>(R.id.navReservasi) ?: return
        val nav3 = activity.findViewById<View>(R.id.navInspeksi) ?: return
        val nav4 = activity.findViewById<View>(R.id.navBengkel) ?: return

        val iv1 = activity.findViewById<ImageView>(R.id.ivNavBeranda)
        val tv1 = activity.findViewById<TextView>(R.id.tvNavBeranda)
        val iv2 = activity.findViewById<ImageView>(R.id.ivNavReservasi)
        val tv2 = activity.findViewById<TextView>(R.id.tvNavReservasi)
        val iv3 = activity.findViewById<ImageView>(R.id.ivNavInspeksi)
        val tv3 = activity.findViewById<TextView>(R.id.tvNavInspeksi)
        val iv4 = activity.findViewById<ImageView>(R.id.ivNavBengkel)
        val tv4 = activity.findViewById<TextView>(R.id.tvNavBengkel)

        val prefs = VehicleRentPrefs.getInstance(activity)
        val isAdmin = prefs.isWorkshopAdmin

        val activeColor = ContextCompat.getColor(
            activity,
            if (isAdmin) R.color.workshop_amber else R.color.primary_navy
        )
        val inactiveColor = ContextCompat.getColor(activity, R.color.text_slate_400)

        // Micro-interactions tactile press feedback
        MotionUtils.applyTactilePressFeedback(nav1)
        MotionUtils.applyTactilePressFeedback(nav2)
        MotionUtils.applyTactilePressFeedback(nav3)
        MotionUtils.applyTactilePressFeedback(nav4)

        if (isAdmin) {
            // MODE PEMANGKU USAHA (BENGKEL & FLEET ADMIN)
            tv1?.text = "Command"
            iv1?.setImageResource(R.drawable.ic_directions_car)

            tv2?.text = "Inspeksi Return"
            iv2?.setImageResource(R.drawable.ic_assignment_turned_in)

            tv3?.text = "Kamus OBD-II"
            iv3?.setImageResource(R.drawable.ic_terminal)

            tv4?.text = "Antrean Stall"
            iv4?.setImageResource(R.drawable.ic_build_circle)

            val isTab1Active = activity is BerandaFleetActivity
            val isTab2Active = activity is InspeksiReturnActivity
            val isTab3Active = activity is ObdDiagnosticActivity
            val isTab4Active = activity is StallBengkelActivity

            highlightTab(iv1, tv1, isTab1Active, activeColor, inactiveColor)
            highlightTab(iv2, tv2, isTab2Active, activeColor, inactiveColor)
            highlightTab(iv3, tv3, isTab3Active, activeColor, inactiveColor)
            highlightTab(iv4, tv4, isTab4Active, activeColor, inactiveColor)

            nav1.setOnClickListener {
                if (!isTab1Active) {
                    val intent = Intent(activity, BerandaFleetActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }

            nav2.setOnClickListener {
                if (!isTab2Active) {
                    val intent = Intent(activity, InspeksiReturnActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }

            nav3.setOnClickListener {
                if (!isTab3Active) {
                    val intent = Intent(activity, ObdDiagnosticActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }

            nav4.setOnClickListener {
                if (!isTab4Active) {
                    val intent = Intent(activity, StallBengkelActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }
        } else {
            // MODE PELANGGAN (PENYEWA)
            tv1?.text = "Katalog"
            iv1?.setImageResource(R.drawable.ic_directions_car)

            tv2?.text = "Form Booking"
            iv2?.setImageResource(R.drawable.ic_calendar_month)

            tv3?.text = "Tiket Reservasi"
            iv3?.setImageResource(R.drawable.ic_qr_code)

            tv4?.text = "Cek Pick-up"
            iv4?.setImageResource(R.drawable.ic_assignment_turned_in)

            val isTab1Active = activity is BerandaFleetActivity
            val isTab2Active = activity is FormPemesananActivity
            val isTab3Active = activity is DetailReservasiActivity
            val isTab4Active = activity is InspeksiPickUpActivity

            highlightTab(iv1, tv1, isTab1Active, activeColor, inactiveColor)
            highlightTab(iv2, tv2, isTab2Active, activeColor, inactiveColor)
            highlightTab(iv3, tv3, isTab3Active, activeColor, inactiveColor)
            highlightTab(iv4, tv4, isTab4Active, activeColor, inactiveColor)

            nav1.setOnClickListener {
                if (!isTab1Active) {
                    val intent = Intent(activity, BerandaFleetActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }

            nav2.setOnClickListener {
                if (!isTab2Active) {
                    val intent = Intent(activity, FormPemesananActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }

            nav3.setOnClickListener {
                if (!isTab3Active) {
                    val intent = Intent(activity, DetailReservasiActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }

            nav4.setOnClickListener {
                if (!isTab4Active) {
                    val intent = Intent(activity, InspeksiPickUpActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    activity.startActivity(intent)
                }
            }
        }
    }

    private fun highlightTab(
        iv: ImageView?,
        tv: TextView?,
        isActive: Boolean,
        activeColor: Int,
        inactiveColor: Int
    ) {
        val color = if (isActive) activeColor else inactiveColor
        iv?.setColorFilter(color)
        tv?.setTextColor(color)
    }
}
