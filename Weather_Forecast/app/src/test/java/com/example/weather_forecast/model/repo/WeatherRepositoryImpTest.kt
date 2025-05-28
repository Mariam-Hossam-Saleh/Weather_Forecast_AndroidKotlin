package com.example.weather_forecast.model.repo

import com.example.weather_forecast.model.database.WeatherLocalDataSource
import com.example.weather_forecast.model.network.WeatherRemoteDataSource
import com.example.weather_forecast.model.pojos.WeatherEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryImpTest {

 private lateinit var repository: WeatherRepositoryImp
 private lateinit var remoteDataSource: WeatherRemoteDataSource
 private lateinit var localDataSource: WeatherLocalDataSource
 private val testDispatcher = StandardTestDispatcher()

 @Before
 fun setup() {
  remoteDataSource = mockk()
  localDataSource = mockk()
  repository = WeatherRepositoryImp(remoteDataSource, localDataSource)
 }

 private fun createWeatherEntity(
  cityName: String,
  lat: Double,
  lon: Double,
  isFavorite: Boolean
 ): WeatherEntity {
  return WeatherEntity(
   dt = System.currentTimeMillis() / 1000,
   lat = lat,
   lon = lon,
   cod = "200",
   cnt = 1,
   dt_txt = "2025-05-29 12:00:00",
   mainTemp = 20.0,
   mainFeels_like = 19.0,
   mainTemp_min = 18.0,
   mainTemp_max = 22.0,
   mainPressure = 1013,
   mainHumidity = 65,
   mainSea_level = 1013,
   mainGrnd_level = 1010,
   weatherId = 800,
   weatherMain = "Clear",
   weatherDescription = "clear sky",
   weatherIcon = "01d",
   clouds = 0,
   windSpeed = 5.0,
   windDeg = 180,
   windGust = 7.0,
   visibility = 10000,
   pop = 0.0,
   rain = 0.0,
   sysPod = "d",
   cityId = 5128581,
   cityName = cityName,
   cityCoordLon = lon,
   cityCoordLat = lat,
   cityCountry = "US",
   cityPopulation = "8175133",
   cityTimezone = "-14400",
   citySunrise = "1625046000",
   citySunset = "1625098800",
   isFavorite = isFavorite
  )
 }

 @Test
 fun `getWeatherForecast remote success returns weather list from remote data source`() = runTest(testDispatcher) {
  // Arrange
  val lat = 40.7128
  val lon = -74.0060
  val apiKey = "e82d172019ed90076e2ec824decb3d40"
  val weatherList = listOf(createWeatherEntity("New York", lat, lon, isFavorite = false))
  coEvery { remoteDataSource.getWeatherOverNetwork(lat, lon, apiKey) } returns weatherList

  // Act
  val result = repository.getWeatherForecast(isRemote = true, lat, lon, apiKey)

  // Assert
  coVerify { remoteDataSource.getWeatherOverNetwork(lat, lon, apiKey) }
  coVerify(exactly = 0) { localDataSource.getAllWeather() }
  assert(result == weatherList)
 }

 @Test
 fun `getWeatherForecast local success returns weather list from local data source`() = runTest(testDispatcher) {
  // Arrange
  val lat = 40.7128
  val lon = -74.0060
  val apiKey = "e82d172019ed90076e2ec824decb3d40"
  val weatherList = listOf(createWeatherEntity("New York", lat, lon, isFavorite = false))
  coEvery { localDataSource.getAllWeather() } returns weatherList

  // Act
  val result = repository.getWeatherForecast(isRemote = false, lat, lon, apiKey)

  // Assert
  coVerify { localDataSource.getAllWeather() }
  coVerify(exactly = 0) { remoteDataSource.getWeatherOverNetwork(any(), any(), any()) }
  assert(result == weatherList)
 }

 @Test(expected = RuntimeException::class)
 fun `getWeatherForecast remote failure throws exception`() = runTest(testDispatcher) {
  // Arrange
  val lat = 40.7128
  val lon = -74.0060
  val apiKey = "e82d172019ed90076e2ec824decb3d40"
  coEvery { remoteDataSource.getWeatherOverNetwork(lat, lon, apiKey) } throws RuntimeException("Network error")

  // Act
  repository.getWeatherForecast(isRemote = true, lat, lon, apiKey)
 }

 @Test
 fun `updateWeatherFavoriteStatus success calls local data source`() = runTest(testDispatcher) {
  // Arrange
  val cityName = "New York"
  val isFavorite = true
  coEvery { localDataSource.updateWeatherFavoriteStatus(cityName, isFavorite) } returns Unit

  // Act
  repository.updateWeatherFavoriteStatus(cityName, isFavorite)

  // Assert
  coVerify { localDataSource.updateWeatherFavoriteStatus(cityName, isFavorite) }
 }

 @Test(expected = RuntimeException::class)
 fun `updateWeatherFavoriteStatus failure throws exception`() = runTest(testDispatcher) {
  // Arrange
  val cityName = "New York"
  val isFavorite = true
  coEvery { localDataSource.updateWeatherFavoriteStatus(cityName, isFavorite) } throws RuntimeException("Database error")

  // Act
  repository.updateWeatherFavoriteStatus(cityName, isFavorite)
 }
}