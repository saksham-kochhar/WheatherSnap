package com.example.wheathersnap

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheathersnap.API.RetrofitInstance
import com.example.wheathersnap.Roomdb.AppDatabase
import com.example.wheathersnap.navndata.CityResult
import com.example.wheathersnap.navndata.ReportEntity
import com.example.wheathersnap.navndata.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WheatherviewModel(application: Application) : AndroidViewModel(application) {

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city
    private val _suggestions =
        MutableStateFlow<List<CityResult>>(emptyList())
    val suggestions: StateFlow<List<CityResult>>
            = _suggestions
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean>
            = _isLoading
    private val _weather =
        MutableStateFlow<WeatherResponse?>(null)

    val weather: StateFlow<WeatherResponse?>
            = _weather
    private val _selectedCity =
        MutableStateFlow("")

    val selectedCity: StateFlow<String>
            = _selectedCity

    private var searchJob: Job? = null
    private val _lastCapturedUri = MutableStateFlow<Uri?>(null)
    val lastCapturedUri: StateFlow<Uri?> = _lastCapturedUri

    private val _originalKb = MutableStateFlow<Long?>(null)
    private val _compressedKb = MutableStateFlow<Long?>(null)
    val originalKb: StateFlow<Long?> = _originalKb
    val compressedKb: StateFlow<Long?> = _compressedKb
    private val db = AppDatabase.getInstance(application)
    private val reportDao = db.reportDao()

    val allReports: StateFlow<List<ReportEntity>> = reportDao
        .getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun searchCity(query: String) {

        searchJob?.cancel()

        if (query.length < 2) {

            _suggestions.value = emptyList()

            _isLoading.value = false

            return
        }

        searchJob = viewModelScope.launch {

            _isLoading.value = true

            delay(300)

            try {

                val response =
                    RetrofitInstance.geocodingApi
                        .searchCity(query)

                _suggestions.value =
                    response.results ?: emptyList()

            } catch (e: Exception) {

                _suggestions.value = emptyList()

            } finally {

                _isLoading.value = false
            }
        }
    }
    fun fetchWeather(cityResult: CityResult) {

        viewModelScope.launch {

            try {

                _selectedCity.value =
                    "${cityResult.name}, ${cityResult.country}"

                val response =
                    RetrofitInstance.weatherApi.getWeatherData(
                            latitude = cityResult.latitude,
                            longitude = cityResult.longitude
                        )

                _weather.value = response

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
    fun getWeatherCondition(code: Int): String {

        return when(code) {

            0 -> "Clear Sky"

            1 -> "Mainly Clear"

            2 -> "Partly Cloudy"

            3 -> "Overcast"

            45 -> "Fog"

            61 -> "Rain"

            71 -> "Snow"

            else -> "Unknown"
        }
    }
    fun clearSuggestions() {
        _suggestions.value = emptyList()
        _isLoading.value = false
    }
    fun onCityChange(newCity: String) {
        _city.value = newCity
        searchCity(newCity)
    }
    fun setCapturedUri(uri: Uri) {
        _lastCapturedUri.value = uri
    }
    fun setImageSizes(original: Long?, compressed: Long?) {
        _originalKb.value = original
        _compressedKb.value = compressed
    }
    fun saveReport(
        city: String,
        weatherCondition: String,
        temperature: String,
        humidity: String,
        wind: String,
        pressure: String,
        imageUri: String,
        originalKb: Long,
        compressedKb: Long,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportDao.insertReport(
                ReportEntity(
                    city = city,
                    weatherCondition = weatherCondition,
                    temperature = temperature,
                    humidity = humidity,
                    wind = wind,
                    pressure = pressure,
                    imageUri = imageUri,
                    originalKb = originalKb,
                    compressedKb = compressedKb,
                    notes = notes
                )
            )
        }
    }
    fun deleteReport(report: ReportEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            reportDao.deleteReport(report)
        } }
}