package com.example.wheathersnap.navndata

data class Georesponse(val results: List<CityResult>?)

data class CityResult(
    val id: Int,
    val name: String,
    val country: String?,
    val admin1: String?,       // state/province
    val latitude: Double,
    val longitude: Double
)

data class WeatherResponse(
    val current: CurrentWeather
)

data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val wind_speed_10m: Double,
    val surface_pressure: Double,
    val weather_code : Int
)

