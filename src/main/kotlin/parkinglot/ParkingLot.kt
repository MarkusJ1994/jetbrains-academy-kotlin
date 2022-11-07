package parkinglot

const val parkCmd: String = "park"
const val leaveCmd: String = "leave"
const val createCmd: String = "create"
const val statusCmd: String = "status"
const val regByColrCmd: String = "reg_by_color"
const val spotByColrCmd: String = "spot_by_color"
const val spotByRegCmd: String = "spot_by_reg"
const val exitCmd: String = "exit"

fun parkingSpot() {
    var spots: MutableMap<Int, ParkingSpotStatus?>? = null

    var input: List<String>
    var cmd: String
    while (true) {
        input = readLine()?.split(" ") ?: listOf();
        cmd = input[0]
        when (cmd) {
            parkCmd -> {
                if (spots == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    park(spots, input[2], input[1])
                }
            }

            leaveCmd -> {
                if (spots == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    leave(spots, input[1].toInt())
                }
            }

            createCmd -> {
                spots = create(input[1].toInt())
            }

            statusCmd -> {
                if (spots == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    status(spots)
                }
            }

            regByColrCmd -> {
                if (spots == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    regByColor(spots, input[1])
                }
            }

            spotByColrCmd -> {
                if (spots == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    spotByColor(spots, input[1])
                }
            }

            spotByRegCmd -> {
                if (spots == null) {
                    println("Sorry, a parking lot has not been created.")
                } else {
                    spotByReg(spots, input[1])
                }
            }

            exitCmd -> break;
            else -> println("Not a recognized command")
        }
    }
}

fun create(parkingSpots: Int): MutableMap<Int, ParkingSpotStatus?> {
    var spots: MutableMap<Int, ParkingSpotStatus?> = mutableMapOf()
    for (i in 1..parkingSpots) {
        spots[i] = null
    }
    println("Created a parking lot with $parkingSpots spots.")
    return spots
}

fun park(spots: MutableMap<Int, ParkingSpotStatus?>, color: String, plate: String) {
    val freeSpot = spots.entries.find { (spot, status) -> status == null }
    if (freeSpot != null) {
        spots[freeSpot.key] = ParkingSpotStatus(color, plate)
        println("$color car parked in spot ${freeSpot.key}.")
    } else {
        println("Sorry, the parking lot is full.")
    }
}

fun leave(spots: MutableMap<Int, ParkingSpotStatus?>, spot: Int) {
    val spotIsFree: Boolean = spots[spot] == null
    if (spotIsFree) {
        println("There is no car in spot $spot.")
    } else {
        spots[spot] = null
        println("Spot $spot is free.")
    }
}

fun status(spots: MutableMap<Int, ParkingSpotStatus?>) {
    val occupiedSpots = spots.filter { (spot, status) -> status != null }
    if (occupiedSpots.isEmpty()) {
        println("Parking lot is empty.")
    } else {
        occupiedSpots.forEach { (spot, status) -> println("$spot $status") }
    }
}

fun regByColor(spots: MutableMap<Int, ParkingSpotStatus?>, color: String) {
    val takenSpotsByColor:  List<ParkingSpotStatus> = spots.values.filterNotNull().filter { status -> status.color.lowercase() == color.lowercase() }
    if (takenSpotsByColor.isEmpty()) {
        println("No cars with color ${color.uppercase()} were found.")
    } else {
        println(takenSpotsByColor.map { status -> status.plate }.reduce { acc, p -> "$acc, $p" })
    }
}

fun spotByColor(spots: MutableMap<Int, ParkingSpotStatus?>, color: String) {
    val takenSpotsByColor = spots.filter { (spot, status) -> status != null && status.color.lowercase() == color.lowercase() }
    if (takenSpotsByColor.isEmpty()) {
        println("No cars with color ${color.uppercase()} were found.")
    } else {
        println(takenSpotsByColor.keys.map { spot -> spot.toString() }.reduce { acc, p -> "$acc, $p" })
    }
}

fun spotByReg(spots: MutableMap<Int, ParkingSpotStatus?>, reg: String) {
    val takenSpotByReg = spots.entries.find { (spot, status) -> status != null && status.plate.lowercase() == reg.lowercase() }
    if (takenSpotByReg == null) {
        println("No cars with registration number $reg were found.")
    } else {
        println(takenSpotByReg.key)
    }
}