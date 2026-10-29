package id.ac.stmi.vehiclerent.model

enum class VehicleStatus(val code: String, val label: String) {
    READY("READY", "Siap Sewa (Lulus QC)"),
    ON_RENT("ON_RENT", "Sedang Disewa"),
    IN_WORKSHOP("IN_WORKSHOP", "Di Bengkel Internal");

    companion object {
        fun fromString(str: String?): VehicleStatus {
            return when (str?.uppercase()) {
                "READY" -> READY
                "ON_RENT", "RENTED", "DISEWA" -> ON_RENT
                "IN_WORKSHOP", "BENGKEL", "WORKSHOP" -> IN_WORKSHOP
                else -> READY
            }
        }
    }
}

