package id.ac.stmi.vehiclerent.util

import android.content.Context
import android.content.SharedPreferences
import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import id.ac.stmi.vehiclerent.model.WorkshopTicketModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local Data & Session Manager via SharedPreferences (Pertemuan 7).
 * Connects Dual-Role Architecture (Customer Portal vs Fleet Owner / Workshop Admin).
 */
class VehicleRentPrefs private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "AutoFleetDirectPrefs"

        // Pertemuan 7 Required Keys
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

        // Dual-Role & Auth Keys
        const val KEY_USER_ROLE = "KEY_USER_ROLE"
        const val KEY_ACTIVE_USER = "KEY_ACTIVE_USER"
        const val KEY_WORKSHOP_TICKETS_JSON = "KEY_WORKSHOP_TICKETS_JSON"

        const val ROLE_RENTER = "RENTER"
        const val ROLE_WORKSHOP_ADMIN = "WORKSHOP_ADMIN"

        const val DEFAULT_STAFF_NAME = "Hendra Wijaya"
        const val DEFAULT_STAFF_ID = "AFD-88219"
        const val DEFAULT_STAGING_BAY = "Staging Bay Area B"
        const val DEFAULT_DRAFT_NAME = "Budi Santoso, S.T."
        const val DEFAULT_DRAFT_WA = "81298765432"
        const val DEFAULT_DRAFT_SIM = "910812345678"

        @Volatile
        private var INSTANCE: VehicleRentPrefs? = null

        fun getInstance(context: Context): VehicleRentPrefs {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VehicleRentPrefs(context).also { INSTANCE = it }
            }
        }
    }

    // Role & Auth Preferences
    var userRole: String
        get() = prefs.getString(KEY_USER_ROLE, ROLE_RENTER) ?: ROLE_RENTER
        set(value) = prefs.edit().putString(KEY_USER_ROLE, value).apply()

    val isWorkshopAdmin: Boolean
        get() = userRole == ROLE_WORKSHOP_ADMIN

    var activeUserName: String
        get() {
            val saved = prefs.getString(KEY_ACTIVE_USER, null)
            if (saved != null) return saved
            return if (isWorkshopAdmin) "Hendra Wijaya (Kepala Bengkel)" else "Budi Santoso (Penyewa)"
        }
        set(value) = prefs.edit().putString(KEY_ACTIVE_USER, value).apply()

    // Staff & Operational Bay
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
        if (!prefs.contains(KEY_WORKSHOP_TICKETS_JSON)) {
            initDefaultWorkshopTickets()
        }
    }

    /**
     * Complete 25 Fleet Units:
     * 14 READY, 8 ON_RENT, 3 IN_WORKSHOP.
     */
    private fun initDefaultFleet() {
        val list = mutableListOf<VehicleModel>()

        // 14 SIAP SEWA (READY)
        list.add(VehicleModel("AF-01", "Toyota Kijang Innova Zenix 2.0 G CVT", "MPV Keluarga (Zenix / Xpander)", "B 1842 KZR", "Automatic • 7 Penumpang • Bensin", "Bay B-04", 24120, 100, 650000L, VehicleStatus.READY, 0.0, "-", "Rem, Oli Sintetik & Ban Baru (33 PSI)", "photo_car_zenix"))
        list.add(VehicleModel("AF-02", "Mitsubishi Xpander Ultimate 1.5 AT", "MPV Keluarga (Zenix / Xpander)", "B 2910 FLT", "Automatic • 7 Penumpang • Bensin", "Bay B-02", 18450, 100, 450000L, VehicleStatus.READY, 0.0, "-", "QC Pass Sistem Kelistrikan & Ban Serep", "photo_car_xpander"))
        list.add(VehicleModel("AF-03", "Toyota Fortuner 2.8 GR Sport 4x4", "Premium SUV (Fortuner / Pajero)", "B 1099 PJR", "Automatic • 7 Penumpang • Diesel", "Bay A-01", 31200, 100, 1100000L, VehicleStatus.READY, 0.0, "-", "4x4 Transfer Case & Suspensi Fresh", "photo_car_fortuner"))
        list.add(VehicleModel("AF-04", "Hyundai Ioniq 5 Signature Long Range", "City Car & EV (Ioniq 5 / Brio)", "B 2188 EVN", "Single-Speed EV • 5 Penumpang • Electric", "Bay E-01", 12300, 100, 950000L, VehicleStatus.READY, 0.0, "-", "State of Health (SoH) Baterai 99.8%", "photo_car_ioniq"))
        list.add(VehicleModel("AF-05", "Honda Brio RS 1.2 CVT Urban", "City Car & EV (Ioniq 5 / Brio)", "B 1455 BRI", "Automatic CVT • 5 Penumpang • Bensin", "Bay C-01", 15200, 100, 350000L, VehicleStatus.READY, 0.0, "-", "Lulus Uji Emisi & AC Dingin 16°C", "photo_car_brio"))
        list.add(VehicleModel("AF-06", "Toyota Kijang Innova Zenix Q Hybrid", "MPV Keluarga (Zenix / Xpander)", "B 1923 ZNX", "e-CVT • 7 Penumpang • Hybrid", "Bay B-05", 21400, 100, 800000L, VehicleStatus.READY, 0.0, "-", "Hybrid Battery Check OK", "photo_car_zenix"))
        list.add(VehicleModel("AF-10", "Mitsubishi Pajero Sport Dakar 4x2", "Premium SUV (Fortuner / Pajero)", "B 1788 MTC", "Automatic 8-Speed • 7 Penumpang • Diesel", "Bay A-02", 28900, 100, 1050000L, VehicleStatus.READY, 0.0, "-", "Brake Pad Baru & Oli Gardan Sintetik", "photo_car_pajero"))
        list.add(VehicleModel("AF-11", "Toyota Kijang Innova Reborn 2.4 V AT", "MPV Keluarga (Zenix / Xpander)", "B 2650 RBN", "Automatic 6-Speed • 7 Penumpang • Diesel", "Bay B-06", 39100, 100, 550000L, VehicleStatus.READY, 0.0, "-", "Engine Clean & Timing Belt OK", "photo_car_zenix"))
        list.add(VehicleModel("AF-12", "Hyundai Ioniq 5 Prime Electric", "City Car & EV (Ioniq 5 / Brio)", "B 2999 EVB", "Single-Speed EV • 5 Penumpang • Electric", "Bay E-02", 8400, 100, 900000L, VehicleStatus.READY, 0.0, "-", "Battery Pack Sealed & Inspected", "photo_car_ioniq"))
        list.add(VehicleModel("AF-13", "Honda HR-V SE 1.5 CVT", "Premium SUV (Fortuner / Pajero)", "B 1320 HRV", "Automatic CVT • 5 Penumpang • Bensin", "Bay A-03", 22100, 100, 650000L, VehicleStatus.READY, 0.0, "-", "Honda Sensing Radar Calibrated", "photo_car_hrv"))
        list.add(VehicleModel("AF-14", "Toyota All New Avanza 1.5 G CVT", "MPV Keluarga (Zenix / Xpander)", "B 2140 AVZ", "Automatic CVT • 7 Penumpang • Bensin", "Bay B-07", 19800, 100, 400000L, VehicleStatus.READY, 0.0, "-", "Ban Baru Tekanan 34 PSI", "photo_car_avanza"))
        list.add(VehicleModel("AF-15", "Toyota Veloz 1.5 Q CVT TSS", "MPV Keluarga (Zenix / Xpander)", "B 2277 XNA", "Automatic CVT • 7 Penumpang • Bensin", "Bay B-08", 17900, 100, 480000L, VehicleStatus.READY, 0.0, "-", "AC Servis & Spooring Lurus", "photo_car_avanza"))
        list.add(VehicleModel("AF-16", "Toyota Fortuner 2.8 VRZ AT", "Premium SUV (Fortuner / Pajero)", "B 1560 YRS", "Automatic 6-Speed • 7 Penumpang • Diesel", "Bay A-04", 14500, 100, 1000000L, VehicleStatus.READY, 0.0, "-", "Eco-Mode Diagnostic Pass", "photo_car_fortuner"))
        list.add(VehicleModel("AF-17", "Honda Brio Satya 1.2 E Urban", "City Car & EV (Ioniq 5 / Brio)", "B 1870 BLN", "Automatic CVT • 5 Penumpang • Bensin", "Bay C-02", 16300, 100, 320000L, VehicleStatus.READY, 0.0, "-", "QC Full Inspeksi 32 Titik Lulus", "photo_car_brio"))

        // 8 SEDANG DISEWA (ON_RENT)
        list.add(VehicleModel("AF-18", "Toyota Kijang Innova Zenix 2.0 V", "MPV Keluarga (Zenix / Xpander)", "B 1123 ZNX", "Automatic • 7 Penumpang • Bensin", "Bay B-09", 26300, 85, 650000L, VehicleStatus.ON_RENT, 0.0, "-", "Rute: Bandung - Jakarta (Family Touring)", "photo_car_zenix"))
        list.add(VehicleModel("AF-19", "Mitsubishi Xpander Cross 1.5 AT", "MPV Keluarga (Zenix / Xpander)", "B 2780 XPC", "Automatic • 7 Penumpang • Bensin", "Bay B-10", 21900, 75, 500000L, VehicleStatus.ON_RENT, 0.0, "-", "Sewa 5 Hari (Lepas Kunci)", "photo_car_xpander"))
        list.add(VehicleModel("AF-20", "Toyota Fortuner 2.4 VRZ AT", "Premium SUV (Fortuner / Pajero)", "B 1490 FTN", "Automatic • 7 Penumpang • Diesel", "Bay A-05", 34500, 90, 950000L, VehicleStatus.ON_RENT, 0.0, "-", "Rental Korporat + Sopir", "photo_car_fortuner"))
        list.add(VehicleModel("AF-21", "Hyundai Ioniq 5 Prime Standard", "City Car & EV (Ioniq 5 / Brio)", "B 2311 EVH", "Single-Speed EV • 5 Penumpang • Electric", "Bay E-03", 16700, 60, 850000L, VehicleStatus.ON_RENT, 0.0, "-", "City Tour Jabodetabek", "photo_car_ioniq"))
        list.add(VehicleModel("AF-22", "Honda Brio Satya 1.2 E CVT", "City Car & EV (Ioniq 5 / Brio)", "B 2844 BRS", "Automatic • 5 Penumpang • Bensin", "Bay C-03", 22100, 80, 320000L, VehicleStatus.ON_RENT, 0.0, "-", "Drop-off Bandara Soetta", "photo_car_brio"))
        list.add(VehicleModel("AF-23", "Toyota Veloz 1.5 Q CVT TSS", "MPV Keluarga (Zenix / Xpander)", "B 1677 VLZ", "Automatic • 7 Penumpang • Bensin", "Bay B-11", 19300, 70, 480000L, VehicleStatus.ON_RENT, 0.0, "-", "Rute Anyer Carita", "photo_car_avanza"))
        list.add(VehicleModel("AF-24", "Mitsubishi Pajero Sport Exceed", "Premium SUV (Fortuner / Pajero)", "B 1880 PJS", "Automatic • 7 Penumpang • Diesel", "Bay A-06", 41200, 85, 900000L, VehicleStatus.ON_RENT, 0.0, "-", "Sewa Mingguan + Driver", "photo_car_pajero"))
        list.add(VehicleModel("AF-25", "Hyundai Ioniq 5 Long Range AWD", "City Car & EV (Ioniq 5 / Brio)", "B 2600 AEV", "Single-Speed EV • 5 Penumpang • Electric", "Bay E-04", 11200, 70, 950000L, VehicleStatus.ON_RENT, 0.0, "-", "Komuter Dalam Kota Jakarta", "photo_car_ioniq"))

        // 3 DI BENGKEL INTERNAL (IN_WORKSHOP)
        list.add(VehicleModel("AF-07", "Mitsubishi Pajero Sport Dakar 4x4", "Premium SUV (Fortuner / Pajero)", "B 1734 SBG", "Automatic • 7 Penumpang • Diesel", "Bay A-07", 45100, 45, 1100000L, VehicleStatus.IN_WORKSHOP, 1.5, "Besok 11:30 WIB (Stall #02)", "Sensor Kecepatan Roda ABS C0035", "photo_car_pajero"))
        list.add(VehicleModel("AF-08", "Hyundai Ioniq 5 Signature AWD", "City Car & EV (Ioniq 5 / Brio)", "B 1144 EVN", "Electric • 5 Penumpang • Electric", "Bay E-05", 29800, 60, 950000L, VehicleStatus.IN_WORKSHOP, 2.5, "Besok 14:00 WIB (Stall #05)", "Komunikasi Modul CAN-Bus U0100", "photo_car_ioniq"))
        list.add(VehicleModel("AF-09", "Toyota Kijang Innova Venturer 2.4", "MPV Keluarga (Zenix / Xpander)", "B 2309 KZD", "Automatic • 7 Penumpang • Diesel", "Bay B-12", 52400, 50, 600000L, VehicleStatus.IN_WORKSHOP, 2.0, "Besok 15:30 WIB (Stall #03)", "Misfire Pengapian Silinder P0300", "photo_car_zenix"))

        saveFleetList(list)
    }

    private fun initDefaultWorkshopTickets() {
        val tickets = listOf(
            WorkshopTicketModel(
                ticketId = "TCK-8801",
                vehicleId = "AF-07",
                vehicleName = "Mitsubishi Pajero Sport Dakar 4x4",
                plateNumber = "B 1734 SBG",
                stallId = "Stall #02",
                dtcCode = "C0035",
                componentIssue = "Left Front Wheel Speed Sensor Module",
                mechanicName = "Mekanik Rizky",
                estimatedHours = 1.2,
                totalCost = 350000L,
                targetReadyTime = "Besok 11:30 WIB (Stall #02)",
                entryOdometer = 45100
            ),
            WorkshopTicketModel(
                ticketId = "TCK-8802",
                vehicleId = "AF-08",
                vehicleName = "Hyundai Ioniq 5 Signature AWD",
                plateNumber = "B 1144 EVN",
                stallId = "Stall #05",
                dtcCode = "U0100",
                componentIssue = "CAN-Bus Main Relay & Grounding Terminal Repair",
                mechanicName = "Mekanik Wahyu",
                estimatedHours = 2.5,
                totalCost = 400000L,
                targetReadyTime = "Besok 14:00 WIB (Stall #05)",
                entryOdometer = 29800
            ),
            WorkshopTicketModel(
                ticketId = "TCK-8803",
                vehicleId = "AF-09",
                vehicleName = "Toyota Kijang Innova Venturer 2.4",
                plateNumber = "B 2309 KZD",
                stallId = "Stall #03",
                dtcCode = "P0300",
                componentIssue = "Ignition Coil Pack & Spark Plug Set",
                mechanicName = "Mekanik Dimas",
                estimatedHours = 2.0,
                totalCost = 450000L,
                targetReadyTime = "Besok 15:30 WIB (Stall #03)",
                entryOdometer = 52400
            )
        )
        saveWorkshopTickets(tickets)
    }

    fun getFleetList(): MutableList<VehicleModel> {
        val jsonStr = prefs.getString(KEY_FLEET_STATE_JSON, null)
        if (jsonStr == null) {
            initDefaultFleet()
            return getFleetList()
        }
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
        // Auto-upgrade if less than 25 units or outdated generic photo assignments
        if (list.size < 25 || list.any { it.name.contains("Brio") && it.imageDrawableName != "photo_car_brio" }) {
            initDefaultFleet()
            return getFleetList()
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
        targetTime: String? = null,
        notes: String? = null
    ) {
        val list = getFleetList()
        val target = list.firstOrNull { it.id == id } ?: return
        target.status = newStatus
        if (odometer != null) target.lastOdometer = odometer
        if (fuel != null) target.fuelPercent = fuel
        if (workshopHours != null) target.workshopEstimateHours = workshopHours
        if (targetTime != null) target.workshopTargetTime = targetTime
        if (notes != null) target.lastCheckNotes = notes
        saveFleetList(list)
    }

    // Workshop Tickets Storage
    fun getWorkshopTickets(): MutableList<WorkshopTicketModel> {
        val jsonStr = prefs.getString(KEY_WORKSHOP_TICKETS_JSON, null) ?: return mutableListOf()
        val list = mutableListOf<WorkshopTicketModel>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    WorkshopTicketModel(
                        ticketId = obj.getString("ticketId"),
                        vehicleId = obj.getString("vehicleId"),
                        vehicleName = obj.getString("vehicleName"),
                        plateNumber = obj.getString("plateNumber"),
                        stallId = obj.getString("stallId"),
                        dtcCode = obj.getString("dtcCode"),
                        componentIssue = obj.getString("componentIssue"),
                        mechanicName = obj.getString("mechanicName"),
                        estimatedHours = obj.getDouble("estimatedHours"),
                        totalCost = obj.getLong("totalCost"),
                        targetReadyTime = obj.getString("targetReadyTime"),
                        entryOdometer = obj.getInt("entryOdometer"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveWorkshopTickets(list: List<WorkshopTicketModel>) {
        val jsonArray = JSONArray()
        for (t in list) {
            val obj = JSONObject().apply {
                put("ticketId", t.ticketId)
                put("vehicleId", t.vehicleId)
                put("vehicleName", t.vehicleName)
                put("plateNumber", t.plateNumber)
                put("stallId", t.stallId)
                put("dtcCode", t.dtcCode)
                put("componentIssue", t.componentIssue)
                put("mechanicName", t.mechanicName)
                put("estimatedHours", t.estimatedHours)
                put("totalCost", t.totalCost)
                put("targetReadyTime", t.targetReadyTime)
                put("entryOdometer", t.entryOdometer)
                put("timestamp", t.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_WORKSHOP_TICKETS_JSON, jsonArray.toString()).apply()
    }

    fun addWorkshopTicket(ticket: WorkshopTicketModel) {
        val list = getWorkshopTickets()
        list.add(0, ticket)
        saveWorkshopTickets(list)
    }

    fun removeWorkshopTicket(ticketId: String) {
        val list = getWorkshopTickets()
        list.removeAll { it.ticketId == ticketId }
        saveWorkshopTickets(list)
    }

    fun releaseTicketAndMarkReady(ticketId: String, newOdometer: Int = -1): VehicleModel? {
        val tickets = getWorkshopTickets()
        val ticket = tickets.firstOrNull { it.ticketId == ticketId } ?: return null
        tickets.remove(ticket)
        saveWorkshopTickets(tickets)

        val fleet = getFleetList()
        val car = fleet.firstOrNull { it.id == ticket.vehicleId } ?: return null
        car.status = VehicleStatus.READY
        car.workshopEstimateHours = 0.0
        car.workshopTargetTime = "-"
        if (newOdometer > 0) car.lastOdometer = newOdometer
        car.lastCheckNotes = "Lulus QC Bengkel (${ticket.stallId}): ${ticket.componentIssue} Selesai"
        saveFleetList(fleet)
        return car
    }

    fun clearAndResetAll() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
        initDefaultFleet()
        initDefaultWorkshopTickets()
    }
}
