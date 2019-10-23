package org.geb

data class Product(val crawlerId: String, val sensor: Sensor, val image: PublishedImage)

data class Sensor(val name: String)