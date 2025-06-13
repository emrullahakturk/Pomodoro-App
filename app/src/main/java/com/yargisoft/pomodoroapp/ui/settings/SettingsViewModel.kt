// app/src/main/java/com/seninuygulamaninadi/ui/settings/SettingsViewModel.kt
package com.yargisoft.pomodoroapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.pomodoroapp.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// Ayarlar ekranının ViewModel'ı
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    // Ayar değerleri için MutableStateFlow'lar
    private val _workTime = MutableStateFlow(SettingsManager.DEFAULT_WORK_TIME_MINUTES)
    val workTime: StateFlow<Int> = _workTime.asStateFlow()

    private val _shortBreakTime = MutableStateFlow(SettingsManager.DEFAULT_SHORT_BREAK_TIME_MINUTES)
    val shortBreakTime: StateFlow<Int> = _shortBreakTime.asStateFlow()

    private val _longBreakTime = MutableStateFlow(SettingsManager.DEFAULT_LONG_BREAK_TIME_MINUTES)
    val longBreakTime: StateFlow<Int> = _longBreakTime.asStateFlow()

    private val _longBreakInterval = MutableStateFlow(SettingsManager.DEFAULT_LONG_BREAK_INTERVAL)
    val longBreakInterval: StateFlow<Int> = _longBreakInterval.asStateFlow()

    // ViewModel başlatıldığında kaydedilmiş ayarları yükle
    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _workTime.value = settingsManager.workTimeMinutes.first()
            _shortBreakTime.value = settingsManager.shortBreakTimeMinutes.first()
            _longBreakTime.value = settingsManager.longBreakTimeMinutes.first()
            _longBreakInterval.value = settingsManager.longBreakInterval.first()
        }
    }

    // Ayar güncelleme fonksiyonları
    fun updateWorkTime(minutes: Int) {
        if (minutes > 0) { // Negatif süreleri engelle
            _workTime.value = minutes
            viewModelScope.launch { settingsManager.saveWorkTimeMinutes(minutes) }
        }
    }

    fun updateShortBreakTime(minutes: Int) {
        if (minutes > 0) {
            _shortBreakTime.value = minutes
            viewModelScope.launch { settingsManager.saveShortBreakTimeMinutes(minutes) }
        }
    }

    fun updateLongBreakTime(minutes: Int) {
        if (minutes > 0) {
            _longBreakTime.value = minutes
            viewModelScope.launch { settingsManager.saveLongBreakTimeMinutes(minutes) }
        }
    }

    fun updateLongBreakInterval(interval: Int) {
        if (interval > 0) {
            _longBreakInterval.value = interval
            viewModelScope.launch { settingsManager.saveLongBreakInterval(interval) }
        }
    }
}