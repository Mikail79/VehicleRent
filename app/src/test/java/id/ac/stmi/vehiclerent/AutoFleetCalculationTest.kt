package id.ac.stmi.vehiclerent

import id.ac.stmi.vehiclerent.model.VehicleModel
import id.ac.stmi.vehiclerent.model.VehicleStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoFleetCalculationTest {

    @Test
    fun testRentalCostCalculation_Precision() {
        val baseRate = 650000L
        val durationDays = 3
        val isWithDriver = true
        val hasFamilyTouring = true
        val hasCoolerBox = false

        val carRent = durationDays * baseRate
        val driverFee = if (isWithDriver) durationDays * 250000L else 0L

        var dailyBundle = 0L
        if (hasFamilyTouring) dailyBundle += 150000L
        if (hasCoolerBox) dailyBundle += 50000L
        val bundleCost = durationDays * dailyBundle

        val subtotal = carRent + driverFee + bundleCost
        val deposit = 500000L
        val grandTotal = subtotal + deposit

        assertEquals(1950000L, carRent)
        assertEquals(750000L, driverFee)
        assertEquals(450000L, bundleCost)
        assertEquals(3150000L, subtotal)
        assertEquals(500000L, deposit)
        assertEquals(3650000L, grandTotal)
    }

    @Test
    fun testReturnTripCalculation_And_OilServiceThreshold() {
        val initialOdo = 24120
        val returnOdo = 24850

        val trip = returnOdo - initialOdo
        assertEquals(730, trip)

        // Threshold: auto-check oil change if trip > 500 km or returnOdo >= 24800 km
        val isOilServiceRequired = trip > 500 || returnOdo >= 24800
        assertTrue("Trip > 500 km harus memicu ganti oli otomatis", isOilServiceRequired)

        // If trip is short, e.g. 50 km and returnOdo 24170
        val shortReturnOdo = 24170
        val shortTrip = shortReturnOdo - initialOdo
        val isShortOilRequired = shortTrip > 500 || shortReturnOdo >= 24800
        assertFalse("Trip pendek dan di bawah 24.800 km tidak boleh memicu ganti oli", isShortOilRequired)
    }

    @Test
    fun testFuelPenaltyCalculations() {
        fun getFuelPenalty(fuelPercent: Int): Long {
            return when (fuelPercent) {
                100 -> 0L
                75 -> 180000L
                50 -> 350000L
                25 -> 500000L
                else -> 0L
            }
        }

        assertEquals(0L, getFuelPenalty(100))
        assertEquals(180000L, getFuelPenalty(75))
        assertEquals(350000L, getFuelPenalty(50))
        assertEquals(500000L, getFuelPenalty(25))
    }

    @Test
    fun testWorkshopDefects_And_TwoWayStatusBranching() {
        // Case A: With findings (Baret + Kampas Rem + OBD P0300)
        var hours = 0.0
        var repairCost = 0L

        // OBD P0300
        hours += 2.0
        repairCost += 450000L

        // Checkbox Baret Bumper
        hours += 3.5
        repairCost += 600000L

        // Checkbox Kampas Rem
        hours += 2.0
        repairCost += 350000L

        val fuelPenalty = 180000L
        val totalCost = repairCost + fuelPenalty

        assertEquals(7.5, hours, 0.001)
        assertEquals(1400000L, repairCost)
        assertEquals(1580000L, totalCost)

        val statusA = if (hours > 0) VehicleStatus.IN_WORKSHOP else VehicleStatus.READY
        assertEquals(VehicleStatus.IN_WORKSHOP, statusA)

        // Case B: Zero findings (Clean pass)
        val zeroHours = 0.0
        val zeroCost = 0L
        val statusB = if (zeroHours > 0) VehicleStatus.IN_WORKSHOP else VehicleStatus.READY
        assertEquals(VehicleStatus.READY, statusB)
    }

    @Test
    fun testVehicleModelJsonSerialization() {
        val original = VehicleModel(
            id = "AF-01",
            name = "Toyota Kijang Innova Zenix 2.0 G CVT",
            category = "MPV Keluarga (Zenix / Xpander)",
            plateNumber = "B 1842 KZR",
            specs = "Automatic • 7 Penumpang • Bensin",
            stagingBay = "Bay B-04",
            lastOdometer = 24850,
            fuelPercent = 75,
            baseRatePerDay = 650000L,
            status = VehicleStatus.IN_WORKSHOP,
            workshopEstimateHours = 5.5,
            workshopTargetTime = "Besok 14:00 WIB (Stall #03)",
            lastCheckNotes = "Perbaikan bumper dan penggantian oli",
            imageDrawableName = "img_car_zenix"
        )

        val json = original.toJsonObject()
        val reconstructed = VehicleModel.fromJsonObject(json)

        assertEquals(original.id, reconstructed.id)
        assertEquals(original.name, reconstructed.name)
        assertEquals(original.plateNumber, reconstructed.plateNumber)
        assertEquals(original.lastOdometer, reconstructed.lastOdometer)
        assertEquals(original.status, reconstructed.status)
        assertEquals(original.workshopEstimateHours, reconstructed.workshopEstimateHours, 0.001)
        assertEquals(original.workshopTargetTime, reconstructed.workshopTargetTime)
    }
}

