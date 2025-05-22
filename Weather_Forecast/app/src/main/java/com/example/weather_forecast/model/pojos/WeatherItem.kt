package com.example.weather_forecast.model.pojos

//@Entity(tableName = "weather_table")
data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys,
    val dt_txt: String
) {
    companion object {
        // WeatherItem to WeatherEntity Mapper
        fun WeatherItem.toWeatherEntity(cityName: String): WeatherEntity {
            return WeatherEntity(
                dt = dt,
                dateText = dt_txt,
                temp = main.temp,
                feelsLike = main.feels_like,
                humidity = main.humidity,
                weatherMain = weather.firstOrNull()?.main ?: "",
                weatherDescription = weather.firstOrNull()?.description ?: "",
                icon = weather.firstOrNull()?.icon ?: "",
                cityName = cityName
            )
        }
    }
}

