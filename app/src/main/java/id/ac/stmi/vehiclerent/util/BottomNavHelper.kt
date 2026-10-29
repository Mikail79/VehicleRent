package id.ac.stmi.vehiclerent.util

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import id.ac.stmi.vehiclerent.BerandaFleetActivity
import id.ac.stmi.vehiclerent.DetailReservasiActivity
import id.ac.stmi.vehiclerent.InspeksiPickUpActivity
import id.ac.stmi.vehiclerent.InspeksiReturnActivity
import id.ac.stmi.vehiclerent.R

/**
 * Connects and coordinates the Bottom Navigation Bar across all 5 screens.
 * Supports active screen highlighting and smooth tactile feedback.
 */
object BottomNavHelper {

    enum class NavTab {
        BERANDA,
        RESERVASI,
        INSPEKSI,
        BENGKEL
    }

    fun setup(activity: Activity, currentTab: NavTab) {
        val navBeranda = activity.findViewById<View>(R.id.navBeranda) ?: return
        val navReservasi = activity.findViewById<View>(R.id.navReservasi) ?: return
        val navInspeksi = activity.findViewById<View>(R.id.navInspeksi) ?: return
        val navBengkel = activity.findViewById<View>(R.id.navBengkel) ?: return

        val ivBeranda = activity.findViewById<ImageView>(R.id.ivNavBeranda)
        val tvBeranda = activity.findViewById<TextView>(R.id.tvNavBeranda)
        val ivReservasi = activity.findViewById<ImageView>(R.id.ivNavReservasi)
        val tvReservasi = activity.findViewById<TextView>(R.id.tvNavReservasi)
        val ivInspeksi = activity.findViewById<ImageView>(R.id.ivNavInspeksi)
        val tvInspeksi = activity.findViewById<TextView>(R.id.tvNavInspeksi)
        val ivBengkel = activity.findViewById<ImageView>(R.id.ivNavBengkel)
        val tvBengkel = activity.findViewById<TextView>(R.id.tvNavBengkel)

        val activeColor = ContextCompat.getColor(activity, R.color.primary_navy)
        val inactiveColor = ContextCompat.getColor(activity, R.color.text_slate_400)

        // Reset and highlight active tab
        ivBeranda?.setColorFilter(if (currentTab == NavTab.BERANDA) activeColor else inactiveColor)
        tvBeranda?.setTextColor(if (currentTab == NavTab.BERANDA) activeColor else inactiveColor)

        ivReservasi?.setColorFilter(if (currentTab == NavTab.RESERVASI) activeColor else inactiveColor)
        tvReservasi?.setTextColor(if (currentTab == NavTab.RESERVASI) activeColor else inactiveColor)

        ivInspeksi?.setColorFilter(if (currentTab == NavTab.INSPEKSI) activeColor else inactiveColor)
        tvInspeksi?.setTextColor(if (currentTab == NavTab.INSPEKSI) activeColor else inactiveColor)

        ivBengkel?.setColorFilter(if (currentTab == NavTab.BENGKEL) activeColor else inactiveColor)
        tvBengkel?.setTextColor(if (currentTab == NavTab.BENGKEL) activeColor else inactiveColor)

        // Micro-interactions tactile press feedback
        MotionUtils.applyTactilePressFeedback(navBeranda)
        MotionUtils.applyTactilePressFeedback(navReservasi)
        MotionUtils.applyTactilePressFeedback(navInspeksi)
        MotionUtils.applyTactilePressFeedback(navBengkel)

        navBeranda.setOnClickListener {
            if (currentTab != NavTab.BERANDA) {
                val intent = Intent(activity, BerandaFleetActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                activity.startActivity(intent)
            }
        }

        navReservasi.setOnClickListener {
            if (currentTab != NavTab.RESERVASI) {
                val intent = Intent(activity, DetailReservasiActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                activity.startActivity(intent)
            }
        }

        navInspeksi.setOnClickListener {
            if (currentTab != NavTab.INSPEKSI) {
                val intent = Intent(activity, InspeksiPickUpActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                activity.startActivity(intent)
            }
        }

        navBengkel.setOnClickListener {
            if (currentTab != NavTab.BENGKEL) {
                val intent = Intent(activity, InspeksiReturnActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                activity.startActivity(intent)
            }
        }
    }
}

