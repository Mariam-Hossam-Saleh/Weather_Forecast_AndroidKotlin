package com.example.weather_forecast.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weather_forecast.model.pojos.WeatherEntity
import com.example.weather_forecast.model.repo.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset
import android.util.Log

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData

    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: WeatherRepository
    private val testDispatcher = StandardTestDispatcher()

    // Observers for LiveData
    private val weatherListObserver = mockk<Observer<List<WeatherEntity>>>(relaxed = true)
    private val isLoadingObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val errorMessageObserver = mockk<Observer<String?>>(relaxed = true)
    private val favoriteStateObserver = mockk<Observer<WeatherEntity?>>(relaxed = true)
    private val favoriteToggleResultObserver = mockk<Observer<Boolean?>>(relaxed = true)

    @Before
    fun setup() {
        // Mock android.util.Log
        mockkStatic(Log::class)
        coEvery { Log.e(any(), any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = HomeViewModel(repository)

        // Observe LiveData
        viewModel.weatherList.observeForever(weatherListObserver)
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.errorMessage.observeForever(errorMessageObserver)
        viewModel.favoriteState.observeForever(favoriteStateObserver)
        viewModel.favoriteToggleResult.observeForever(favoriteToggleResultObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.weatherList.removeObserver(weatherListObserver)
        viewModel.isLoading.removeObserver(isLoadingObserver)
        viewModel.errorMessage.removeObserver(errorMessageObserver)
        viewModel.favoriteState.removeObserver(favoriteStateObserver)
        viewModel.favoriteToggleResult.removeObserver(favoriteToggleResultObserver)
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
    fun `fetchWeather success updates weatherList and loading state`() = runTest {
        // Arrange
        val lat = 40.7128
        val lon = -74.0060
        val weatherList = listOf(createWeatherEntity("New York", lat, lon, isFavorite = false))
        val todayMidnight = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond()
        coEvery {
            repository.getWeatherForecast(
                isRemote = true,
                lat = lat,
                lon = lon,
                apiKey = "e82d172019ed90076e2ec824decb3d40"
            )
        } returns weatherList
        coEvery { repository.clearOldWeather(todayMidnight) } returns Unit
        coEvery { repository.insertWeatherList(weatherList) } returns Unit

        // Act
        viewModel.fetchWeather(lat, lon)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { isLoadingObserver.onChanged(true) }
        coVerify { repository.clearOldWeather(todayMidnight) }
        coVerify { repository.insertWeatherList(weatherList) }
        coVerify { weatherListObserver.onChanged(weatherList) }
        coVerify { isLoadingObserver.onChanged(false) }
        coVerify { errorMessageObserver.onChanged(null) }
    }

    @Test
    fun `fetchWeather failure updates errorMessage and loading state`() = runTest {
        // Arrange
        val lat = 40.7128
        val lon = -74.0060
        val exception = RuntimeException("Network error")
        coEvery {
            repository.getWeatherForecast(
                isRemote = true,
                lat = lat,
                lon = lon,
                apiKey = "e82d172019ed90076e2ec824decb3d40"
            )
        } throws exception

        // Act
        viewModel.fetchWeather(lat, lon)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { isLoadingObserver.onChanged(true) }
        coVerify { errorMessageObserver.onChanged("Failed to fetch weather: Network error") }
        coVerify { isLoadingObserver.onChanged(false) }
        coVerify(exactly = 0) { repository.clearOldWeather(any()) }
        coVerify(exactly = 0) { repository.insertWeatherList(any()) }
        coVerify(exactly = 0) { weatherListObserver.onChanged(any()) }
    }

    @Test
    fun `toggleFavoriteStatus non-favorite to favorite updates favoriteState and weatherList`() = runTest {
        // Arrange
        val cityName = "New York"
        val lat = 40.7128
        val lon = -74.0060
        val currentFavorite = createWeatherEntity(cityName, lat, lon, isFavorite = false)
        val updatedFavorite = currentFavorite.copy(isFavorite = true)
        val weatherList = listOf(updatedFavorite)
        coEvery { repository.getFavoriteStateForCity(cityName) } returnsMany listOf(currentFavorite, updatedFavorite)
        coEvery { repository.updateWeatherFavoriteStatus(cityName, true) } returns Unit
        coEvery { repository.getCityWeather(lat, lon) } returns weatherList

        // Act
        viewModel.toggleFavoriteStatus(cityName, lat, lon)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { isLoadingObserver.onChanged(true) }
        coVerify { repository.updateWeatherFavoriteStatus(cityName, true) }
        coVerify { favoriteStateObserver.onChanged(updatedFavorite) }
        coVerify { favoriteToggleResultObserver.onChanged(true) }
        coVerify { weatherListObserver.onChanged(weatherList) }
        coVerify { isLoadingObserver.onChanged(false) }
        coVerify { favoriteToggleResultObserver.onChanged(null) }
        coVerify { errorMessageObserver.onChanged(null) }
    }

    @Test
    fun `toggleFavoriteStatus failure updates errorMessage`() = runTest {
        // Arrange
        val cityName = "New York"
        val lat = 40.7128
        val lon = -74.0060
        val exception = RuntimeException("Database error")
        coEvery { repository.getFavoriteStateForCity(cityName) } throws exception

        // Act
        viewModel.toggleFavoriteStatus(cityName, lat, lon)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { isLoadingObserver.onChanged(true) }
        coVerify { errorMessageObserver.onChanged("Failed to update favorite status: Database error") }
        coVerify { isLoadingObserver.onChanged(false) }
        coVerify { favoriteToggleResultObserver.onChanged(null) }
        coVerify(exactly = 0) { favoriteStateObserver.onChanged(any()) }
        coVerify(exactly = 0) { weatherListObserver.onChanged(any()) }
        coVerify(exactly = 0) { repository.updateWeatherFavoriteStatus(any(), any()) }
    }
}