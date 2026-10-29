package id.ac.stmi.vehiclerent.model

import java.io.Serializable

/**
 * Standardized OBD-II Diagnostic Trouble Code (DTC) representation.
 * Complies with SAE J2012 standard and AutoFleet Direct flat-rate workshop matrix.
 */
data class DtcModel(
    val code: String,
    val system: String,
    val category: String,
    val description: String,
    val recommendedAction: String,
    val flatRateHours: Double,
    val partCost: Long,
    val severity: String // CRITICAL, WARNING, NORMAL
) : Serializable

