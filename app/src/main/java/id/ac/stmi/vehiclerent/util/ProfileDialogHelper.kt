package id.ac.stmi.vehiclerent.util

import android.app.Activity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import id.ac.stmi.vehiclerent.R

object ProfileDialogHelper {

    fun showProfileDialog(activity: Activity, onProfileUpdated: (() -> Unit)? = null) {
        val prefs = VehicleRentPrefs.getInstance(activity)
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_staff_profile, null)

        val etStaffName = dialogView.findViewById<EditText>(R.id.etProfileStaffName)
        val etStaffId = dialogView.findViewById<EditText>(R.id.etProfileStaffId)
        val etStagingBay = dialogView.findViewById<EditText>(R.id.etProfileStagingBay)
        val switchDarkMode = dialogView.findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val btnReset = dialogView.findViewById<Button>(R.id.btnResetSharedPreferences)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveProfile)

        etStaffName.setText(prefs.staffName)
        etStaffId.setText(prefs.staffId)
        etStagingBay.setText(prefs.stagingBay)
        switchDarkMode.isChecked = prefs.isDarkMode

        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(dialogView)
            .create()

        btnReset.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setTitle("Reset Seluruh Data?")
                .setMessage("Semua riwayat sewa, draf formulir, dan status armada di SharedPreferences akan dibersihkan kembali ke pengaturan bawaan.")
                .setIcon(R.drawable.ic_report_problem)
                .setPositiveButton("Reset Sekarang") { _, _ ->
                    prefs.clearAndResetAll()
                    Toast.makeText(activity, "SharedPreferences berhasil di-reset!", Toast.LENGTH_SHORT).show()
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
                etStaffName.error = "Nama petugas tidak boleh kosong"
                return@setOnClickListener
            }

            val darkChanged = prefs.isDarkMode != isDark

            prefs.staffName = newName
            prefs.staffId = if (newId.isNotEmpty()) newId else VehicleRentPrefs.DEFAULT_STAFF_ID
            prefs.stagingBay = if (newBay.isNotEmpty()) newBay else VehicleRentPrefs.DEFAULT_STAGING_BAY
            prefs.isDarkMode = isDark

            Toast.makeText(activity, "Profil berhasil disimpan ke SharedPreferences", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            onProfileUpdated?.invoke()

            if (darkChanged) {
                applyDarkMode(isDark)
            }
        }

        dialog.show()
    }

    fun applyDarkMode(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}

