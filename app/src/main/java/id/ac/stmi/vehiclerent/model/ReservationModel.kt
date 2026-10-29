package id.ac.stmi.vehiclerent.model

import java.io.Serializable

data class ReservationModel(
    val bookingCode: String = "#AF-2026-089",
    val vehicle: VehicleModel,
    val renterName: String,
    val renterPhone: String,
    val renterIdAndSim: String,
    val durationDays: Int,
    val pickupDate: String,
    val isWithDriver: Boolean,
    val isPackageBundling: Boolean,
    val hasFamilyTouring: Boolean,
    val hasDashcamGps: Boolean,
    val hasCoolerBox: Boolean,
    val paymentMethod: String,
    val baseRentTotal: Long,
    val driverTotal: Long,
    val bundleTotal: Long,
    val subtotalRent: Long,
    val depositAmount: Long = 500000L,
    val totalPayment: Long,
    val qcPassStaff: String = "Mekanik Hendra",
    var initialOdometer: Int = 24120,
    var initialFuel: Int = 100,
    var returnOdometer: Int = 24850,
    var returnFuel: Int = 75,
    var inspectionStage: String = "READY_FOR_PICKUP"
) : Serializable

