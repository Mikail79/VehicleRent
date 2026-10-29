package id.ac.stmi.vehiclerent.util

import android.content.Context
import id.ac.stmi.vehiclerent.model.DtcModel
import org.json.JSONArray

/**
 * SAE J2012 OBD-II String Parser and Relational DTC Repository.
 * Automatically analyzes 5-character alphanumeric trouble codes.
 */
object ObdParser {

    private var cachedDictionary: List<DtcModel>? = null

    /**
     * Loads the 20+ DTC standardized catalog from assets/dtc_dictionary.json
     */
    fun getDtcDictionary(context: Context): List<DtcModel> {
        cachedDictionary?.let { return it }

        val list = mutableListOf<DtcModel>()
        try {
            val jsonString = context.assets.open("dtc_dictionary.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DtcModel(
                        code = obj.getString("code"),
                        system = obj.getString("system"),
                        category = obj.getString("category"),
                        description = obj.getString("description"),
                        recommendedAction = obj.getString("recommended_action"),
                        flatRateHours = obj.getDouble("flat_rate_hours"),
                        partCost = obj.getLong("part_cost"),
                        severity = obj.getString("severity")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cachedDictionary = list
        return list
    }

    /**
     * Finds exact match in dictionary or parses dynamically via SAE J2012 standard.
     */
    fun parseOrLookup(context: Context, rawCode: String): DtcModel {
        val cleanCode = rawCode.trim().uppercase()
        val dict = getDtcDictionary(context)

        val exactMatch = dict.firstOrNull { it.code.equals(cleanCode, ignoreCase = true) }
        if (exactMatch != null) return exactMatch

        // Dynamic SAE J2012 Parser
        if (cleanCode.length != 5) {
            return DtcModel(
                code = if (cleanCode.isNotEmpty()) cleanCode else "P0300",
                system = "Powertrain",
                category = "General Diagnostics",
                description = "Custom DTC: $cleanCode",
                recommendedAction = "Sensor Check & Harness Continuity Test",
                flatRateHours = 1.0,
                partCost = 250000L,
                severity = "NORMAL"
            )
        }

        val d1 = cleanCode[0]
        val d2 = cleanCode[1]
        val d3 = cleanCode[2]
        val d45 = cleanCode.substring(3, 5)

        val system = when (d1) {
            'P' -> "Powertrain"
            'C' -> "Chassis"
            'B' -> "Body"
            'U' -> "Network"
            else -> "Powertrain"
        }

        val codeStandard = when (d2) {
            '0' -> "SAE Universal Standard"
            '1' -> "Manufacturer OEM Specific"
            else -> "SAE Extended"
        }

        val (category, defaultHours, defaultCost) = when (d1) {
            'P' -> when (d3) {
                '1', '2' -> Triple("Fuel & Air Metering (Sensors/Injectors)", 1.5, 380000L)
                '3' -> Triple("Ignition System & Cylinder Misfire", 2.0, 450000L)
                '4' -> Triple("Auxiliary Emission Controls (Catalytic/EVAP)", 2.2, 550000L)
                '5' -> Triple("Vehicle Speed & Idle Control System", 1.4, 280000L)
                '6' -> Triple("Computer & Output Auxiliary Circuits", 1.8, 320000L)
                '7', '8' -> Triple("Transmission & Solenoid Valve Controls", 3.5, 950000L)
                else -> Triple("General Powertrain Unit", 1.5, 350000L)
            }
            'C' -> when (d3) {
                '0' -> Triple("Brake, ABS & Hydraulic Circuits", 1.3, 350000L)
                '1' -> Triple("Steering & Sensor Angle Servomotor", 1.6, 420000L)
                '2' -> Triple("Suspension & Stability Dynamics", 1.8, 480000L)
                else -> Triple("General Chassis & Kaki-Kaki", 1.4, 340000L)
            }
            'B' -> when (d3) {
                '0' -> Triple("SRS Airbag & Passenger Restraints", 2.0, 550000L)
                '1' -> Triple("HVAC Climate & Cabin Electronics", 1.5, 300000L)
                '2' -> Triple("Central Door Locks & Lighting Controls", 1.2, 260000L)
                else -> Triple("General Body Accessories", 1.2, 280000L)
            }
            'U' -> when (d3) {
                '0' -> Triple("High-Speed CAN-Bus & ECU Interconnect", 2.5, 450000L)
                '1' -> Triple("Gateway & Telematics Gateway Line", 2.0, 390000L)
                else -> Triple("Network Communication Bus", 2.0, 400000L)
            }
            else -> Triple("General Automotive Diagnostic", 1.5, 300000L)
        }

        val severity = if (defaultHours >= 2.0 || defaultCost >= 450000L) "CRITICAL" else "WARNING"

        return DtcModel(
            code = cleanCode,
            system = system,
            category = category,
            description = "$system DTC [$codeStandard] Subsistem-$d3 ID-$d45",
            recommendedAction = "Pemeriksaan $category & Reset ECU Scanner",
            flatRateHours = defaultHours,
            partCost = defaultCost,
            severity = severity
        )
    }
}

