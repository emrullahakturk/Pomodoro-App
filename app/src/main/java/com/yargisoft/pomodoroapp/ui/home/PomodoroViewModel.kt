package com.yargisoft.pomodoroapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Pomodoro durumlarını tanımlayan bir enum sınıfı
enum class PomodoroState {
    WORKING,
    SHORT_BREAK,
    LONG_BREAK,
    PAUSED,
    STOPPED
}

// ViewModel'ı Hilt'in enjekte edebilmesi için @HiltViewModel ile işaretliyoruz.
// Constructor'a @Inject ekleyerek Hilt'in bağımlılıkları sağlamasını sağlıyoruz.
@HiltViewModel
class PomodoroViewModel @Inject constructor(
    // İleride bağımlılıklar eklemek istersek buraya ekleyeceğiz (örneğin Ayarlar Yöneticisi)
) : ViewModel() {

    // --- StateFlow'lar (UI'ın gözlemleyeceği veriler) ---

    // Geri sayım için kalan süreyi tutar (milisaniye cinsinden).
    private val _timeMillis = MutableStateFlow(DEFAULT_WORK_TIME_MILLIS)
    val timeMillis: StateFlow<Long> = _timeMillis.asStateFlow()

    // Pomodoro'nun mevcut durumunu tutar (WORKING, SHORT_BREAK, vb.).
    private val _pomodoroState = MutableStateFlow(PomodoroState.STOPPED)
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    // Tamamlanan çalışma turlarını sayar.
    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    // --- Dahili Değişkenler ---

    private var timerJob: Job? = null // Zamanlayıcı coroutine'ini tutar
    private var isPaused = false // Zamanlayıcının duraklatılıp duraklatılmadığını kontrol eder

    // --- Zamanlayıcı Başlatma Fonksiyonları ---

    fun startTimer() {
        if (timerJob?.isActive == true && !isPaused) return // Zaten çalışıyorsa veya duraklatılmışsa tekrar başlatma

        isPaused = false // Duraklatma durumunu sıfırla

        // Mevcut duruma göre zamanlayıcıyı başlat
        when (_pomodoroState.value) {
            PomodoroState.STOPPED, PomodoroState.PAUSED -> {
                // Eğer durdurulmuş veya duraklatılmışsa, mevcut süreden devam et
                startCountdown(_timeMillis.value)
                _pomodoroState.value = PomodoroState.WORKING // Başlangıçta çalışma modunda
            }
            PomodoroState.WORKING -> startCountdown(_timeMillis.value)
            PomodoroState.SHORT_BREAK -> startCountdown(_timeMillis.value)
            PomodoroState.LONG_BREAK -> startCountdown(_timeMillis.value)
        }
    }

    private fun startCountdown(initialTime: Long) {
        timerJob?.cancel() // Önceki zamanlayıcı işini iptal et (varsa)
        _timeMillis.value = initialTime // Başlangıç süresini ayarla

        timerJob = viewModelScope.launch {
            _pomodoroState.value = PomodoroState.WORKING // Varsayılan olarak çalışma durumu
            while (_timeMillis.value > 0 && !isPaused) {
                delay(1000) // Her saniye bekle
                _timeMillis.value -= 1000 // Süreyi azalt
            }

            if (_timeMillis.value <= 0) {
                // Süre bittiğinde durumu değiştir
                handleTimerEnd()
            }
        }
    }

    // --- Zamanlayıcı Kontrol Fonksiyonları ---

    fun pauseTimer() {
        if (timerJob?.isActive == true) {
            timerJob?.cancel() // Zamanlayıcıyı durdur
            isPaused = true // Duraklatma durumunu ayarla
            _pomodoroState.value = PomodoroState.PAUSED // Durumu PAUSED olarak güncelle
        }
    }

    fun stopTimer() {
        timerJob?.cancel() // Zamanlayıcıyı tamamen durdur
        _timeMillis.value = DEFAULT_WORK_TIME_MILLIS // Başlangıç süresine dön
        _pomodoroState.value = PomodoroState.STOPPED // Durumu STOPPED olarak güncelle
        _currentRound.value = 1 // Tur sayısını sıfırla
        isPaused = false // Duraklatma durumunu sıfırla
    }

    // --- Zamanlayıcı Süresi Bittiğinde Yapılacaklar ---

    private fun handleTimerEnd() {
        when (_pomodoroState.value) {
            PomodoroState.WORKING -> {
                _currentRound.value++ // Tur sayısını artır
                if (_currentRound.value % LONG_BREAK_INTERVAL == 0) {
                    // Uzun mola zamanı
                    _timeMillis.value = DEFAULT_LONG_BREAK_TIME_MILLIS
                    _pomodoroState.value = PomodoroState.LONG_BREAK
                } else {
                    // Kısa mola zamanı
                    _timeMillis.value = DEFAULT_SHORT_BREAK_TIME_MILLIS
                    _pomodoroState.value = PomodoroState.SHORT_BREAK
                }
                startCountdown(_timeMillis.value) // Yeni zamanlayıcıyı başlat
            }
            PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                // Mola bitti, çalışma moduna dön
                _timeMillis.value = DEFAULT_WORK_TIME_MILLIS
                _pomodoroState.value = PomodoroState.WORKING
                startCountdown(_timeMillis.value) // Yeni zamanlayıcıyı başlat
            }
            PomodoroState.PAUSED, PomodoroState.STOPPED -> {
                // Bu durumlar timerEnd'de normalde çağrılmaz, ama hata kontrolü için burada.
            }
        }
    }

    // --- Sabitler ---
    companion object {
        const val DEFAULT_WORK_TIME_MILLIS = 25 * 60 * 1000L // 25 dakika
        const val DEFAULT_SHORT_BREAK_TIME_MILLIS = 5 * 60 * 1000L // 5 dakika
        const val DEFAULT_LONG_BREAK_TIME_MILLIS = 15 * 60 * 1000L // 15 dakika
        private const val LONG_BREAK_INTERVAL = 4 // Her 4 çalışma turunda bir uzun mola
    }
}