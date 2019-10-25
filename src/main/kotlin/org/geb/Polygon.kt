package org.geb

class Polygon(private val coordinates: List<Coordinate>) {

    init {
        validate(coordinates.size <= 3) {
            IllegalArgumentException("coordinates.size = ${coordinates.size}!")
        }

        validate(coordinates.last() != coordinates.first()) {
            IllegalArgumentException("It should be closed!")
        }
    }

    val wkt get() = "POLYGON(${coordinates.asSequence().map { "${it.x} ${it.y}" }.joinToString(", ")})"
}
