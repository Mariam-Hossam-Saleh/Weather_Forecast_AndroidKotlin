package com.example.weather_forecast.model.pojos

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
        fun WeatherItem.toWeatherEntity(city: City, cod: String, cnt: Int,lat: Double, lon:Double): WeatherEntity {
            val firstWeather = weather.firstOrNull()
            return WeatherEntity(
                dt = dt,
                lat = lat,
                lon = lon,
                cod = cod,
                cnt = cnt,
                dt_txt = dt_txt,
                mainTemp = main.temp,
                mainFeels_like = main.feels_like,
                mainTemp_min = main.temp_min,
                mainTemp_max = main.temp_max,
                mainPressure = main.pressure,
                mainHumidity = main.humidity,
                mainSea_level = main.sea_level,
                mainGrnd_level = main.grnd_level,
                weatherId = firstWeather?.id ?: 0,
                weatherMain = firstWeather?.main ?: "Unknown",
                weatherDescription = firstWeather?.description ?: "Unknown",
                weatherIcon = firstWeather?.icon ?: "01d",
                clouds = clouds.all,
                windSpeed = wind.speed,
                windDeg = wind.deg,
                windGust = wind.gust,
                visibility = visibility,
                pop = pop,
                rain = rain?.rain,
                sysPod = sys.pod,
                cityId = city.id,
                cityName = city.name,
                cityCoordLon = city.coord.lon,
                cityCoordLat = city.coord.lat,
                cityCountry = city.country,
                cityPopulation = city.population.toString(),
                cityTimezone = city.timezone.toString(),
                citySunrise = city.sunrise.toString(),
                citySunset = city.sunset.toString()
            )
        }

    }
}

