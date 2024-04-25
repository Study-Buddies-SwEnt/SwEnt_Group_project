package com.github.se.studybuddies.data

data class Location(val latitude: Double, val longitude: Double, val locationName: String) {
  companion object {
    fun fromString(locationString: String): Location {
      val parts = locationString.split(",")
      if (parts.size < 2) {
        return Location(0.0, 0.0, "")
      }
      return Location(
          parts[0].toDouble(), parts[1].toDouble(), parts.subList(2, parts.size).joinToString(","))
    }
  }

  override fun toString(): String {
    return "$latitude,$longitude,$locationName"
  }
}
