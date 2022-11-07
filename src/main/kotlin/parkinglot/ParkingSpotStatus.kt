package parkinglot

data class ParkingSpotStatus(val color: String, val plate: String) {
    override fun toString(): String {
        return "${this.plate} ${this.color}"
    }
}