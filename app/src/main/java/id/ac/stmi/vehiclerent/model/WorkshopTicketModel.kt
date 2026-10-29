package id.ac.stmi.vehiclerent.model

import java.io.Serializable

/**
 * Represents an active internal workshop repair ticket / stall allocation.
 */
data class WorkshopTicketModel(
    val ticketId: String,
    val vehicleId: String,
    val vehicleName: String,
    val plateNumber: String,
    val stallId: String,
    val dtcCode: String,
    val componentIssue: String,
    val mechanicName: String,
    val estimatedHours: Double,
    val totalCost: Long,
    val targetReadyTime: String,
    val entryOdometer: Int,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

