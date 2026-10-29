package id.ac.stmi.vehiclerent.util

import android.content.Context
import android.content.SharedPreferences
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import org.json.JSONArray
import org.json.JSONObject

class VehicleRentPrefs private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "AutoFleetDirectPrefs"

        const val KEY_STAFF_NAME = "KEY_STAFF_NAME"
        const val KEY_STAFF_ID = "KEY_STAFF_ID"
        const val KEY_STAGING_BAY = "KEY_STAGING_BAY"
        const val KEY_DARK_MODE = "KEY_DARK_MODE"

        const val KEY_DRAFT_RENTER_NAME = "KEY_DRAFT_RENTER_NAME"
        const val KEY_DRAFT_WA = "KEY_DRAFT_WA"
        const val KEY_DRAFT_SIM = "KEY_DRAFT_SIM"

        const val KEY_FLEET_STATE_JSON = "KEY_FLEET_STATE_JSON"
        const val KEY_INITIAL_ODOMETER = "KEY_INITIAL_ODOMETER"
        const val KEY_INITIAL_FUEL = "KEY_INITIAL_FUEL"
        const val KEY_ACTIVE_CAR_ID = "KEY_ACTIVE_CAR_ID"

        const val DEFAULT_STAFF_NAME = "Hendra Wijaya"
        const val DEFAULT_STAFF_ID = "AFD-88219"
        const val DEFAULT_STAGING_BAY = "Staging Bay Area B"
        const val DEFAULT_DRAFT_NAME = "Budi Santoso, S.T."
        const val DEFAULT_DRAFT_WA = "812-9876-5432"
        const val DEFAULT_DRAFT_SIM = "3171052408910002 / SIM: 910812345678"

        @Volatile
        private var INSTANCE: VehicleRentPrefs? = null

        fun getInstance(context: Context): VehicleRentPrefs {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VehicleRentPrefs(context).also { INSTANCE = it }
            }
        }
    }

    var staffName: String
        get() = prefs.getString(KEY_STAFF_NAME, DEFAULT_STAFF_NAME) ?: DEFAULT_STAFF_NAME
        set(value) = prefs.edit().putString(KEY_STAFF_NAME, value).apply()

    var staffId: String
        get() = prefs.getString(KEY_STAFF_ID, DEFAULT_STAFF_ID) ?: DEFAULT_STAFF_ID
        set(value) = prefs.edit().putString(KEY_STAFF_ID, value).apply()

    var stagingBay: String
        get() = prefs.getString(KEY_STAGING_BAY, DEFAULT_STAGING_BAY) ?: DEFAULT_STAGING_BAY
        set(value) = prefs.edit().putString(KEY_STAGING_BAY, value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var draftRenterName: String
        get() = prefs.getString(KEY_DRAFT_RENTER_NAME, DEFAULT_DRAFT_NAME) ?: DEFAULT_DRAFT_NAME
        set(value) = prefs.edit().putString(KEY_DRAFT_RENTER_NAME, value).apply()

    var draftWa: String
        get() = prefs.getString(KEY_DRAFT_WA, DEFAULT_DRAFT_WA) ?: DEFAULT_DRAFT_WA
        set(value) = prefs.edit().putString(KEY_DRAFT_WA, value).apply()

    var draftSim: String
        get() = prefs.getString(KEY_DRAFT_SIM, DEFAULT_DRAFT_SIM) ?: DEFAULT_DRAFT_SIM
        set(value) = prefs.edit().putString(KEY_DRAFT_SIM, value).apply()

    var initialOdometer: Int
        get() = prefs.getInt(KEY_INITIAL_ODOMETER, 24120)
        set(value) = prefs.edit().putInt(KEY_INITIAL_ODOMETER, value).apply()

    var initialFuel: Int
        get() = prefs.getInt(KEY_INITIAL_FUEL, 100)
        set(value) = prefs.edit().putInt(KEY_INITIAL_FUEL, value).apply()

    var activeCarId: String
        get() = prefs.getString(KEY_ACTIVE_CAR_ID, "AF-01") ?: "AF-01"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CAR_ID, value).apply()

    init {
        if (!prefs.contains(KEY_FLEET_STATE_JSON)) {
            initDefaultFleet()
        }
    }

    private fun initDefaultFleet() {
        val defaultList = listOf(
            VehicleModel(
                id = "AF-01",
                name = "Toyota Kijang Innova Zenix 2.0 G CVT",
                category = "MPV Keluarga (Zenix / Xpander)",
                plateNumber = "B 1842 KZR",
                specs = "Automatic • 7 Penumpang • Bensin",
                stagingBay = "Bay B-04",
                lastOdometer = 24120,
                fuelPercent = 100,
                baseRatePerDay = 650000L,
                status = VehicleStatus.READY,
                workshopEstimateHours = 0.0,
                workshopTargetTime = "-",
                lastCheckNotes = "Rem, Oli Sintetik & Ban Baru (Tekanan 33 PSI)",
                imageDrawableName = "img_car_zenix"
            ),
            VehicleModel(
                id = "AF-02",
                name = "Mitsubishi Xpander Ultimate 1.5 AT",
                category = "MPV Keluarga (Zenix / Xpander)",
                plateNumber = "B 2910 FLT",
                specs = "Automatic • 7 Penumpang • Bensin",
                stagingBay = "Bay B-02",
                lastOdometer = 18450,
                fuelPercent = 100,
                baseRatePerDay = 450000L,
                status = VehicleStatus.READY,
                workshopEstimateHours = 0.0,
                workshopTargetTime = "-",
                lastCheckNotes = "QC Pass Sistem Kelistrikan & Ban Serep",
                imageDrawableName = "img_car_xpander"
            ),
            VehicleModel(
                id = "AF-03",
                name = "Toyota Fortuner 2.8 GR Sport 4x4",
                category = "Premium SUV (Fortuner / Pajero)",
                plateNumber = "B 1099 PJR",
                specs = "Automatic • 7 Penumpang • Diesel",
                stagingBay = "Bay A-01",
                lastOdometer = 31200,
                fuelPercent = 100,
                baseRatePerDay = 1100000L,
                status = VehicleStatus.READY,
                workshopEstimateHours = 0.0,
                workshopTargetTime = "-",
                lastCheckNotes = "Servis Kaki-kaki & Spooring Balans Selesai",
                imageDrawableName = "img_car_fortuner"
            ),
            VehicleModel(
                id = "AF-04",
                name = "Mitsubishi Pajero Sport Dakar 4x2 AT",
                category = "Premium SUV (Fortuner / Pajero)",
                plateNumber = "B 1442 BSK",
                specs = "Automatic • 7 Penumpang • Diesel",
                stagingBay = "Bay A-03",
                lastOdometer = 29800,
                fuelPercent = 80,
                baseRatePerDay = 1050000L,
                status = VehicleStatus.ON_RENT,
                workshopEstimateHours = 0.0,
                workshopTargetTime = "-",
                lastCheckNotes = "Unit dalam masa sewa sampai 30 Okt",
                imageDrawableName = "img_car_fortuner"
            ),
            VehicleModel(
                id = "AF-05",
                name = "Hyundai Ioniq 5 Signature Long Range EV",
                category = "City Car & EV (Ioniq 5 / Brio)",
                plateNumber = "B 1905 EVN",
                specs = "Single Speed • 5 Penumpang • Electric",
                stagingBay = "Bay C-01",
                lastOdometer = 14200,
                fuelPercent = 100,
                baseRatePerDay = 900000L,
                status = VehicleStatus.READY,
                workshopEstimateHours = 0.0,
                workshopTargetTime = "-",
                lastCheckNotes = "Baterai 100% SOH 99.8% - Fast Charging OK",
                imageDrawableName = "img_car_ioniq"
            ),
            VehicleModel(
                id = "AF-06",
                name = "Honda Brio RS Urbanite 1.2 CVT",
                category = "City Car & EV (Ioniq 5 / Brio)",
                plateNumber = "B 2731 EKO",
                specs = "CVT • 5 Penumpang • Bensin",
                stagingBay = "Bay C-03",
                lastOdometer = 38400,
                fuelPercent = 60,
                baseRatePerDay = 350000L,
                status = VehicleStatus.IN_WORKSHOP,
                workshopEstimateHours = 3.5,
                workshopTargetTime = "Hari ini 17:00 WIB (Stall #02)",
                lastCheckNotes = "Penggantian Kampas Rem & Tune Up Rutin",
                imageDrawableName = "img_car_xpander"
            ),
            VehicleModel(
                id = "AF-07",
                name = "Toyota Kijang Innova Reborn 2.4 V AT",
                category = "MPV Keluarga (Zenix / Xpander)",
                plateNumber = "B 1288 TXA",
                specs = "Automatic • 7 Penumpang • Diesel",
                stagingBay = "Bay B-05",
                lastOdometer = 45200,
                fuelPercent = 75,
                baseRatePerDay = 550000L,
                status = VehicleStatus.ON_RENT,
                workshopEstimateHours = 0.0,
                workshopTargetTime = "-",
                lastCheckNotes = "Dalam perjalanan luar kota",
                imageDrawableName = "img_car_zenix"
            ),
            VehicleModel(
                id = "AF-08",
                name = "Honda HR-V 1.5 SE CVT",
                category = "Premium SUV (Fortuner / Pajero)",
                plateNumber = "B 1654 RFS",
                specs = "CVT • 5 Penumpang • Bensin",
                stagingBay = "Bay A-04",
                lastOdometer = 22100,
                fuelPercent = 50,
                baseRatePerDay = 700000L,
                status = VehicleStatus.IN_WORKSHOP,
                workshopEstimateHours = 2.0,
                workshopTargetTime = "Besok 10:00 WIB (Stall #04)",
                lastCheckNotes = "Pembersihan Evaporator AC & Filter Kabin",
                imageDrawableName = "img_car_fortuner"
            )
        )
        saveFleetList(defaultList)
    }

    fun getFleetList(): MutableList<VehicleModel> {
        val jsonStr = prefs.getString(KEY_FLEET_STATE_JSON, null) ?: return mutableListOf()
        val list = mutableListOf<VehicleModel>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(VehicleModel.fromJsonObject(obj))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveFleetList(list: List<VehicleModel>) {
        val jsonArray = JSONArray()
        for (v in list) {
            jsonArray.put(v.toJsonObject())
        }
        prefs.edit().putString(KEY_FLEET_STATE_JSON, jsonArray.toString()).apply()
    }

    fun getVehicleById(id: String): VehicleModel? {
        return getFleetList().firstOrNull { it.id == id }
    }

    fun updateVehicleStatus(
        id: String,
        newStatus: VehicleStatus,
        odometer: Int? = null,
        fuel: Int? = null,
        workshopHours: Double? = null,
        targetTime: String? = null
    ) {
        val list = getFleetList()
        val target = list.firstOrNull { it.id == id } ?: return
        target.status = newStatus
        if (odometer != null) target.lastOdometer = odometer
        if (fuel != null) target.fuelPercent = fuel
        if (workshopHours != null) target.workshopEstimateHours = workshopHours
        if (targetTime != null) target.workshopTargetTime = targetTime
        saveFleetList(list)
    }

    fun clearAndResetAll() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
        initDefaultFleet()
    }
}

