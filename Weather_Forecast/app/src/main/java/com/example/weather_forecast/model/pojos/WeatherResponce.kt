package com.example.weather_forecast.model.pojos

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City
)

data class CurrentWeatherResponce(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int) {
    companion object {
        // WeatherItem to WeatherEntity Mapper
        fun CurrentWeatherResponce.toCurrentWeatherEntity(cityName: String,lat: Double, lon:Double): CurrentWeatherEntity {
            return CurrentWeatherEntity(
                coordLon = coord.lon,
                coordLat = coord.lat,
                weatherId = weather[0].id,
                weatherMain = weather[0].main,
                weatherDescription = weather[0].description,
                weatherIcon = weather[0].icon,
                base = base,
                mainTemp = main.temp,
                mainFeels_like = main.feels_like,
                mainTemp_min = main.temp_min,
                mainTemp_max = main.temp_max,
                mainPressure = main.pressure,
                mainHumidity = main.humidity,
                mainSea_level = main.sea_level,
                mainGrnd_level = main.grnd_level,
                visibility = visibility,
                windSpeed = wind.speed,
                windDeg = wind.deg,
                windGust = wind.gust,
                rain = rain?.rain,
                clouds = clouds.all,
                dt = dt,
                sysType = sys.type,
                sysId = sys.id,
                sysCountry = sys.country,
                sysSunrise = sys.sunrise,
                sysSunset = sys.sunset,
                timezone = timezone,
                cityId = id,
                cityName = this.name,
                cod = cod,
                lat = lat,
                lon = lon,
            )
        }
    }
}