import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.weather_forecast.model.network.WeatherRemoteDataSource
import com.example.weather_forecast.model.network.WeatherService
import com.example.weather_forecast.model.pojos.CurrentWeatherResponce.Companion.toCurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.pojos.CurrentWeatherEntity
import com.example.weather_forecast.model.pojos.WeatherItem.Companion.toWeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSourceImp(
    private val weatherService: WeatherService,
    private val context: Context
) : WeatherRemoteDataSource {

    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): List<WeatherEntity> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable(context)) {
            throw Exception("No internet connection")
        } else {
            try {
                val response = weatherService.getWeatherForecast(
                    lat = lat,
                    lon = lon,
                    apiKey = apiKey,
                    units = units
                )
                Log.d("WeatherRemoteDataSource", "Weather forecast response: $response")

                response.list.mapNotNull { weatherItem ->
                    try {
                        weatherItem.toWeatherEntity(response.city,response.cod,response.cnt)
                    } catch (e: Exception) {
                        null
                    }
                }

            } catch (e: Exception) {
                throw Exception("Failed to fetch forecast", e)
                emptyList()
            }
        }
    }

    override suspend fun getCurrentWeatherOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): CurrentWeatherEntity = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable(context)) {
            throw Exception("No internet connection")
        } else {
            try {
                val response = weatherService.getCurrentWeather(
                    lat = lat,
                    lon = lon,
                    apiKey = apiKey,
                    units = units
                )
                Log.d("WeatherRemoteDataSource", "Current weather forecast response: $response")
                response.toCurrentWeatherEntity(response.cityName)

            } catch (e: Exception) {
                throw Exception("Failed to fetch current weather", e)
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}