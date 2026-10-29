package id.ac.stmi.vehiclerent.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import id.ac.stmi.vehiclerent.R
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import id.ac.stmi.vehiclerent.util.MotionUtils
import java.text.NumberFormat
import java.util.Locale

class VehicleAdapter(
    private val context: Context,
    private var vehicleList: List<VehicleModel>,
    private val onBookClicked: (VehicleModel) -> Unit,
    private val onItemLongClicked: (VehicleModel, View) -> Boolean
) : BaseAdapter() {

    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    fun updateData(newList: List<VehicleModel>) {
        vehicleList = newList
        notifyDataSetChanged()
    }

    override fun getCount(): Int = vehicleList.size

    override fun getItem(position: Int): VehicleModel = vehicleList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_vehicle_card, parent, false)

        val vehicle = getItem(position)

        val ivImage = view.findViewById<ImageView>(R.id.ivVehicleImage)
        val llStatusBadge = view.findViewById<LinearLayout>(R.id.llStatusBadge)
        val ivStatusIcon = view.findViewById<ImageView>(R.id.ivStatusIcon)
        val tvStatusText = view.findViewById<TextView>(R.id.tvStatusText)
        val tvVehicleFuel = view.findViewById<TextView>(R.id.tvVehicleFuel)

        val tvVehicleName = view.findViewById<TextView>(R.id.tvVehicleName)
        val tvVehicleSpecs = view.findViewById<TextView>(R.id.tvVehicleSpecs)
        val tvPlateNumber = view.findViewById<TextView>(R.id.tvPlateNumber)
        val tvStagingBay = view.findViewById<TextView>(R.id.tvStagingBay)

        val tvLastOdometer = view.findViewById<TextView>(R.id.tvLastOdometer)
        val tvCheckNotes = view.findViewById<TextView>(R.id.tvCheckNotes)
        val tvReadyBadge = view.findViewById<TextView>(R.id.tvReadyBadge)

        val tvBaseRate = view.findViewById<TextView>(R.id.tvBaseRate)
        val btnBook = view.findViewById<Button>(R.id.btnBookVehicle)

        // Populate Data
        tvVehicleName.text = vehicle.name
        tvVehicleSpecs.text = vehicle.specs
        tvPlateNumber.text = vehicle.plateNumber
        tvStagingBay.text = "Staging: ${vehicle.stagingBay}"
        tvVehicleFuel.text = "BBM: ${vehicle.fuelPercent}%"
        tvLastOdometer.text = "${currencyFormat.format(vehicle.lastOdometer)} km"
        tvCheckNotes.text = vehicle.lastCheckNotes
        tvBaseRate.text = "Rp ${currencyFormat.format(vehicle.baseRatePerDay)}"

        // Specific 1-to-1 Image Mapping for each Car Type
        val imageRes = when {
            vehicle.imageDrawableName == "photo_car_brio" ||
            vehicle.name.contains("Brio", ignoreCase = true) -> R.drawable.photo_car_brio

            vehicle.imageDrawableName == "photo_car_avanza" ||
            vehicle.name.contains("Avanza", ignoreCase = true) ||
            vehicle.name.contains("Veloz", ignoreCase = true) ||
            vehicle.name.contains("Xenia", ignoreCase = true) -> R.drawable.photo_car_avanza

            vehicle.imageDrawableName == "photo_car_pajero" ||
            vehicle.name.contains("Pajero", ignoreCase = true) -> R.drawable.photo_car_pajero

            vehicle.imageDrawableName == "photo_car_hrv" ||
            vehicle.name.contains("HR-V", ignoreCase = true) -> R.drawable.photo_car_hrv

            vehicle.imageDrawableName == "photo_car_fortuner" ||
            vehicle.name.contains("Fortuner", ignoreCase = true) -> R.drawable.photo_car_fortuner

            vehicle.imageDrawableName == "photo_car_ioniq" ||
            vehicle.name.contains("Ioniq", ignoreCase = true) ||
            vehicle.name.contains("EV", ignoreCase = true) -> R.drawable.photo_car_ioniq

            vehicle.imageDrawableName == "photo_car_xpander" ||
            vehicle.name.contains("Xpander", ignoreCase = true) -> R.drawable.photo_car_xpander

            else -> R.drawable.photo_car_zenix
        }
        ivImage.setImageResource(imageRes)

        val prefs = id.ac.stmi.vehiclerent.util.VehicleRentPrefs.getInstance(context)
        val isAdmin = prefs.isWorkshopAdmin

        // Precision Telemetry Tag Styling (High-Contrast Obsidian Glass Tag, No AI Slop Pill)
        when (vehicle.status) {
            VehicleStatus.READY -> {
                ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                ivStatusIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#4ADE80"))
                tvStatusText.text = "SIAP SEWA • LULUS QC"
                tvStatusText.setTextColor(Color.parseColor("#4ADE80"))
                tvReadyBadge.text = "Siap Jalan"
                tvReadyBadge.setTextColor(ContextCompat.getColor(context, R.color.status_ready_text))

                btnBook.isEnabled = true
                btnBook.text = if (isAdmin) "Alokasi Sewa" else "Pilih & Pesan"
                btnBook.setBackgroundResource(R.drawable.bg_button_primary)
            }
            VehicleStatus.ON_RENT -> {
                ivStatusIcon.setImageResource(R.drawable.ic_commute)
                ivStatusIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#38BDF8"))
                tvStatusText.text = "DISEWA • DI JALAN"
                tvStatusText.setTextColor(Color.parseColor("#38BDF8"))
                tvReadyBadge.text = "Di Jalan"
                tvReadyBadge.setTextColor(ContextCompat.getColor(context, R.color.status_rented_text))

                btnBook.isEnabled = true
                btnBook.text = if (isAdmin) "Inspeksi Return" else "Simulasi Return"
                btnBook.setBackgroundResource(R.drawable.bg_button_secondary)
            }
            VehicleStatus.IN_WORKSHOP -> {
                ivStatusIcon.setImageResource(R.drawable.ic_build_circle)
                ivStatusIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#F87171"))
                tvStatusText.text = "BENGKEL • STALL SERVICE"
                tvStatusText.setTextColor(Color.parseColor("#F87171"))
                tvReadyBadge.text = "Perlu Servis"
                tvReadyBadge.setTextColor(ContextCompat.getColor(context, R.color.status_workshop_text))

                if (isAdmin) {
                    btnBook.isEnabled = true
                    btnBook.text = "Kelola di Stall"
                    btnBook.setBackgroundResource(R.drawable.bg_button_amber)
                } else {
                    btnBook.isEnabled = false
                    btnBook.text = "Terkunci (Bengkel)"
                    btnBook.setBackgroundColor(ContextCompat.getColor(context, R.color.border_slate_300))
                }
            }
        }

        MotionUtils.applyTactilePressFeedback(btnBook)
        btnBook.setOnClickListener {
            onBookClicked(vehicle)
        }

        view.setOnLongClickListener {
            onItemLongClicked(vehicle, view)
        }

        return view
    }
}

