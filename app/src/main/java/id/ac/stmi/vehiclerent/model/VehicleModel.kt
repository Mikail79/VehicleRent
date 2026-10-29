package id.ac.stmi.vehiclerent.model

import org.json.JSONObject
import java.io.Serializable

data class VehicleModel(
    val id: String,
    val name: String,
    val category: String,
    val plateNumber: String,
    val specs: String,
    val stagingBay: String,
    var lastOdometer: Int,
    var fuelPercent: Int,
    val baseRatePerDay: Long,
    var status: VehicleStatus = VehicleStatus.READY,
    var workshopEstimateHours: Double = 0.0,
    var workshopTargetTime: String = "-",
    var lastCheckNotes: String = "Rem, Oli Sintetik & Ban Baru (Tekanan 33 PSI)",
    val imageDrawableName: String = "photo_car_zenix"
) : Serializable {

    fun toJsonObject(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("category", category)
        json.put("plateNumber", plateNumber)
        json.put("specs", specs)
        json.put("stagingBay", stagingBay)
        json.put("lastOdometer", lastOdometer)
        json.put("fuelPercent", fuelPercent)
        json.put("baseRatePerDay", baseRatePerDay)
        json.put("status", status.code)
        json.put("workshopEstimateHours", workshopEstimateHours)
        json.put("workshopTargetTime", workshopTargetTime)
        json.put("lastCheckNotes", lastCheckNotes)
        json.put("imageDrawableName", imageDrawableName)
        return json
    }

    companion object {
        fun fromJsonObject(json: JSONObject): VehicleModel {
            return VehicleModel(
                id = json.optString("id", "AF-01"),
                name = json.optString("name", "Toyota Kijang Innova Zenix 2.0 G CVT"),
                category = json.optString("category", "MPV Keluarga (Zenix / Xpander)"),
                plateNumber = json.optString("plateNumber", "B 1842 KZR"),
                specs = json.optString("specs", "Automatic • 7 Penumpang • Bensin"),
                stagingBay = json.optString("stagingBay", "Bay B-04"),
                lastOdometer = json.optInt("lastOdometer", 24120),
                fuelPercent = json.optInt("fuelPercent", 100),
                baseRatePerDay = json.optLong("baseRatePerDay", 650000L),
                status = VehicleStatus.fromString(json.optString("status", "READY")),
                workshopEstimateHours = json.optDouble("workshopEstimateHours", 0.0),
                workshopTargetTime = json.optString("workshopTargetTime", "-"),
                lastCheckNotes = json.optString("lastCheckNotes", "Rem, Oli Sintetik & Ban Baru"),
                imageDrawableName = json.optString("imageDrawableName", "img_car_zenix")
            )
        }
    }
}

