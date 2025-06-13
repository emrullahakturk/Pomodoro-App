// app/src/main/java/com/seninuygulamaninadi/data/SettingsManager.kt
package com.yargisoft.pomodoroapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore örneğini oluştur. Sadece bir tane olmalı.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pomodoro_settings")

// Uygulama ayarlarını yöneten sınıf.
@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {

    // Preferences Keys
    private object PreferencesKeys {
        val WORK_TIME_MINUTES = intPreferencesKey("work_time_minutes")
        val SHORT_BREAK_TIME_MINUTES = intPreferencesKey("short_break_time_minutes")
        val LONG_BREAK_TIME_MINUTES = intPreferencesKey("long_break_time_minutes")
        val LONG_BREAK_INTERVAL = intPreferencesKey("long_break_interval")
    }

    // --- Ayarları Oku ---

    // Çalışma süresi akışını sağlar (dakika cinsinden)
    val workTimeMinutes: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.WORK_TIME_MINUTES] ?: DEFAULT_WORK_TIME_MINUTES
        }

    // Kısa mola süresi akışını sağlar (dakika cinsinden)
    val shortBreakTimeMinutes: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SHORT_BREAK_TIME_MINUTES] ?: DEFAULT_SHORT_BREAK_TIME_MINUTES
        }

    // Uzun mola süresi akışını sağlar (dakika cinsinden)
    val longBreakTimeMinutes: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_TIME_MINUTES] ?: DEFAULT_LONG_BREAK_TIME_MINUTES
        }

    // Uzun mola aralığı akışını sağlar (tur sayısı cinsinden)
    val longBreakInterval: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_INTERVAL] ?: DEFAULT_LONG_BREAK_INTERVAL
        }

    // --- Ayarları Yaz ---

    suspend fun saveWorkTimeMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_TIME_MINUTES] = minutes
        }
    }

    suspend fun saveShortBreakTimeMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHORT_BREAK_TIME_MINUTES] = minutes
        }
    }

    suspend fun saveLongBreakTimeMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_TIME_MINUTES] = minutes
        }
    }

    suspend fun saveLongBreakInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_INTERVAL] = interval
        }
    }

    // --- Varsayılan Değerler ---
    companion object {
        const val DEFAULT_WORK_TIME_MINUTES = 25
        const val DEFAULT_SHORT_BREAK_TIME_MINUTES = 5
        const val DEFAULT_LONG_BREAK_TIME_MINUTES = 15
        const val DEFAULT_LONG_BREAK_INTERVAL = 4
    }
}