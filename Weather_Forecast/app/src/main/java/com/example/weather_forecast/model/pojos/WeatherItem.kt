package com.example.weather_forecast.model.pojos

//@Entity(tableName = "weather_table")
data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: Weather,
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
        fun WeatherItem.toWeatherEntity(cityName: City,cod: String, cnt: Int): WeatherEntity {
            return WeatherEntity(
                dt = dt,
                dt_txt = dt_txt,
                mainTemp = main.temp,
                mainFeels_like = main.feels_like,
                mainTemp_min = main.temp_min,
                mainTemp_max = main.temp_max,
                mainPressure = main.pressure,
                mainHumidity = main.humidity,
                mainSea_level = main.sea_level,
                mainGrnd_level = main.grnd_level,
                weatherId = weather.id,
                weatherMain = weather.main,
                weatherDescription = weather.description,
                weatherIcon = weather.icon,
                clouds = clouds.all,
                windSpeed = wind.speed,
                windDeg = wind.deg,
                windGust = wind.gust,
                visibility = visibility,
                pop = pop,
                rain = rain?.rain,
                sysPod = sys.pod,
                cod = cod,
                cnt = cnt,
                cityId = cityName.id,
                cityName = cityName.name,
                cityCoordLon = cityName.coord.lon,
                cityCoordLat = cityName.coord.lat,
                cityCountry = cityName.country,
                cityPopulation = cityName.population,
                cityTimezone = cityName.timezone,
                citySunrise = cityName.sunrise,
                citySunset = cityName.sunset,
            )
        }
    }
}

