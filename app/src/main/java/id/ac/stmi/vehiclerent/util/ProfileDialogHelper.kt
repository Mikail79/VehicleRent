package id.ac.stmi.vehiclerent.util

import android.app.Activity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import id.ac.stmi.vehiclerent.R

/**
 * Manages Dual-Role Switcher and Staff Profile authentication via SharedPreferences (Pertemuan 7).
 */
object ProfileDialogHelper {

    fun showProfileDialog(activity: Activity, onProfileUpdated: (() -> Unit)? = null) {
        val prefs = VehicleRentPrefs.getInstance(activity)
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_staff_profile, null)

        val cardRoleRenter = dialogView.findViewById<LinearLayout>(R.id.cardRoleRenter)
        val rbRoleRenter = dialogView.findViewById<RadioButton>(R.id.rbRoleRenter)
        val cardRoleWorkshop = dialogView.findViewById<LinearLayout>(R.id.cardRoleWorkshop)
        val rbRoleWorkshop = dialogView.findViewById<RadioButton>(R.id.rbRoleWorkshop)

        val etStaffName = dialogView.findViewById<EditText>(R.id.etProfileStaffName)
        val etStaffId = dialogView.findViewById<EditText>(R.id.etProfileStaffId)
        val etStagingBay = dialogView.findViewById<EditText>(R.id.etProfileStagingBay)
        val switchDarkMode = dialogView.findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val btnReset = dialogView.findViewById<Button>(R.id.btnResetSharedPreferences)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveProfile)

        // Set initial role state
        var currentSelectedRole = prefs.userRole
        val isWorkshop = currentSelectedRole == VehicleRentPrefs.ROLE_WORKSHOP_ADMIN
        rbRoleRenter.isChecked = !isWorkshop
        rbRoleWorkshop.isChecked = isWorkshop

        cardRoleRenter.isSelected = !isWorkshop
        cardRoleWorkshop.isSelected = isWorkshop

        etStaffName.setText(prefs.staffName)
        etStaffId.setText(prefs.staffId)
        etStagingBay.setText(prefs.stagingBay)
        switchDarkMode.isChecked = prefs.isDarkMode

        MotionUtils.applyTactilePressFeedback(cardRoleRenter)
        MotionUtils.applyTactilePressFeedback(cardRoleWorkshop)
        MotionUtils.applyTactilePressFeedback(btnReset)
        MotionUtils.applyTactilePressFeedback(btnSave)

        cardRoleRenter.setOnClickListener {
            currentSelectedRole = VehicleRentPrefs.ROLE_RENTER
            rbRoleRenter.isChecked = true
            rbRoleWorkshop.isChecked = false
            cardRoleRenter.isSelected = true
            cardRoleWorkshop.isSelected = false
            etStaffName.setText(prefs.draftRenterName.ifEmpty { "Budi Santoso, S.T." })
            etStaffId.setText("CUST-9108")
        }

        cardRoleWorkshop.setOnClickListener {
            currentSelectedRole = VehicleRentPrefs.ROLE_WORKSHOP_ADMIN
            rbRoleRenter.isChecked = false
            rbRoleWorkshop.isChecked = true
            cardRoleRenter.isSelected = false
            cardRoleWorkshop.isSelected = true
            etStaffName.setText("Hendra Wijaya (Kepala Bengkel)")
            etStaffId.setText("AFD-88219")
        }

        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(dialogView)
            .create()

        btnReset.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setTitle("Reset Seluruh Data & Sesi?")
                .setMessage("Semua riwayat sewa, antrean bengkel, dan dataset SharedPreferences akan dikembalikan ke setelan awal.")
                .setIcon(R.drawable.ic_report_problem)
                .setPositiveButton("Reset Sekarang") { _, _ ->
                    prefs.clearAndResetAll()
                    Toast.makeText(activity, "Data & sesi SharedPreferences berhasil di-reset!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    onProfileUpdated?.invoke()
                    activity.recreate()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        btnSave.setOnClickListener {
            val newName = etStaffName.text.toString().trim()
            val newId = etStaffId.text.toString().trim()
            val newBay = etStagingBay.text.toString().trim()
            val isDark = switchDarkMode.isChecked

            if (newName.isEmpty()) {
                etStaffName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            val darkChanged = prefs.isDarkMode != isDark
            val roleChanged = prefs.userRole != currentSelectedRole

            prefs.userRole = currentSelectedRole
            prefs.activeUserName = newName
            prefs.staffName = newName
            prefs.staffId = if (newId.isNotEmpty()) newId else VehicleRentPrefs.DEFAULT_STAFF_ID
            prefs.stagingBay = if (newBay.isNotEmpty()) newBay else VehicleRentPrefs.DEFAULT_STAGING_BAY
            prefs.isDarkMode = isDark

            val roleMsg = if (currentSelectedRole == VehicleRentPrefs.ROLE_WORKSHOP_ADMIN) {
                "Aktif: Mode Pemangku Usaha & Bengkel"
            } else {
                "Aktif: Mode Pelanggan (Penyewa)"
            }
            Toast.makeText(activity, "$roleMsg • SharedPreferences Tersimpan", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
            onProfileUpdated?.invoke()

            if (darkChanged) {
                applyDarkMode(isDark)
            }

            if (roleChanged) {
                activity.recreate()
            }
        }

        dialog.show()
    }

    fun setupRoleBanner(activity: Activity, onRoleChanged: (() -> Unit)? = null) {
        val banner = activity.findViewById<LinearLayout>(R.id.llRoleBannerContainer) ?: return
        val ivIcon = activity.findViewById<android.widget.ImageView>(R.id.ivRoleBannerIcon)
        val tvTitle = activity.findViewById<android.widget.TextView>(R.id.tvRoleBannerTitle)
        val btnSwitch = activity.findViewById<android.widget.TextView>(R.id.btnQuickSwitchRole)

        val prefs = VehicleRentPrefs.getInstance(activity)
        val isWorkshop = prefs.isWorkshopAdmin

        if (isWorkshop) {
            banner.setBackgroundColor(androidx.core.content.ContextCompat.getColor(activity, R.color.workshop_amber_light))
            ivIcon?.apply {
                setImageResource(R.drawable.ic_build_circle)
                imageTintList = android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(activity, R.color.workshop_amber)
                )
            }
            tvTitle?.apply {
                text = "MODE PEMANGKU USAHA • ${prefs.staffName}"
                setTextColor(androidx.core.content.ContextCompat.getColor(activity, R.color.workshop_amber_dark))
            }
            btnSwitch?.apply {
                text = "GANTI ROLE"
                setBackgroundResource(R.drawable.bg_badge_workshop)
                setTextColor(androidx.core.content.ContextCompat.getColor(activity, R.color.status_workshop_text))
            }
        } else {
            banner.setBackgroundColor(androidx.core.content.ContextCompat.getColor(activity, R.color.primary_navy_light))
            ivIcon?.apply {
                setImageResource(R.drawable.ic_person)
                imageTintList = android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(activity, R.color.primary_navy)
                )
            }
            tvTitle?.apply {
                text = "MODE PELANGGAN • ${prefs.draftRenterName}"
                setTextColor(androidx.core.content.ContextCompat.getColor(activity, R.color.primary_navy))
            }
            btnSwitch?.apply {
                text = "GANTI ROLE"
                setBackgroundResource(R.drawable.bg_badge_rented)
                setTextColor(androidx.core.content.ContextCompat.getColor(activity, R.color.primary_navy))
            }
        }

        banner.setOnClickListener {
            showProfileDialog(activity) {
                setupRoleBanner(activity, onRoleChanged)
                onRoleChanged?.invoke()
            }
        }

        btnSwitch?.setOnClickListener {
            val newRole = if (prefs.isWorkshopAdmin) VehicleRentPrefs.ROLE_RENTER else VehicleRentPrefs.ROLE_WORKSHOP_ADMIN
            prefs.userRole = newRole
            val label = if (newRole == VehicleRentPrefs.ROLE_WORKSHOP_ADMIN) "Mode Pemangku Usaha (Bengkel)" else "Mode Pelanggan (Penyewa)"
            android.widget.Toast.makeText(activity, "Beralih ke $label", android.widget.Toast.LENGTH_SHORT).show()
            setupRoleBanner(activity, onRoleChanged)
            onRoleChanged?.invoke()
            activity.recreate()
        }
    }

    fun applyDarkMode(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
