package org.geb

class Coordinate(val x: Double, val y: Double) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Coordinate -> other.x == x && other.y == y
            else -> false
        }
    }
}
