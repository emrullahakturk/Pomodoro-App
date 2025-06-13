package com.yargisoft.pomodoroapp.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.pomodoroapp.data.SettingsManager // SettingsManager'ı import etmeyi unutma
import com.yargisoft.pomodoroapp.util.NotificationHelper
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
    private val application: Application, // Application context'ini enjekte et
    private val settingsManager: SettingsManager // SettingsManager'ı buraya enjekte ediyoruz
) : ViewModel() {

    // Ayarlardan okunan ve UI'a aktarılan ilk süreler
    // Bunları public yapıyoruz ki HomeScreen'deki TimerDisplay kullanabilsin.
    var initialWorkTimeMillis: Long = SettingsManager.DEFAULT_WORK_TIME_MINUTES * 60 * 1000L
        private set // Dışarıdan sadece okunabilir

    var initialShortBreakTimeMillis: Long = SettingsManager.DEFAULT_SHORT_BREAK_TIME_MINUTES * 60 * 1000L
        private set

    var initialLongBreakTimeMillis: Long = SettingsManager.DEFAULT_LONG_BREAK_TIME_MINUTES * 60 * 1000L
        private set

    private var currentLongBreakInterval: Int = SettingsManager.DEFAULT_LONG_BREAK_INTERVAL

    // --- StateFlow'lar (UI'ın gözlemleyeceği veriler) ---

    // Geri sayım için kalan süreyi tutar (milisaniye cinsinden).
    private val _timeMillis = MutableStateFlow(initialWorkTimeMillis) // Başlangıç süresini buradan al
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

    // ViewModel başlatıldığında ayarları yükle
    init {
        viewModelScope.launch {
            settingsManager.workTimeMinutes.collect { minutes ->
                initialWorkTimeMillis = minutes * 60 * 1000L
                // Eğer zamanlayıcı durmuşsa (başlangıç durumunda), başlangıç süresini güncelle
                if (_pomodoroState.value == PomodoroState.STOPPED) {
                    _timeMillis.value = initialWorkTimeMillis
                }
            }
        }
        viewModelScope.launch {
            settingsManager.shortBreakTimeMinutes.collect { minutes ->
                initialShortBreakTimeMillis = minutes * 60 * 1000L
            }
        }
        viewModelScope.launch {
            settingsManager.longBreakTimeMinutes.collect { minutes ->
                initialLongBreakTimeMillis = minutes * 60 * 1000L
            }
        }
        viewModelScope.launch {
            settingsManager.longBreakInterval.collect { interval ->
                currentLongBreakInterval = interval
            }
        }
    }


    // --- Zamanlayıcı Başlatma Fonksiyonları ---

    fun startTimer() {
        if (timerJob?.isActive == true && !isPaused) return // Zaten çalışıyorsa veya duraklatılmışsa tekrar başlatma

        isPaused = false // Duraklatma durumunu sıfırla

        // Mevcut duruma göre zamanlayıcıyı başlat
        when (_pomodoroState.value) {
            PomodoroState.STOPPED -> {
                // Eğer durdurulmuşsa, çalışma süresini başlangıç ayarlarına göre başlat
                _timeMillis.value = initialWorkTimeMillis
                startCountdown(_timeMillis.value)
                _pomodoroState.value = PomodoroState.WORKING
            }
            PomodoroState.PAUSED -> {
                // Duraklatılmışsa, mevcut süreden devam et
                startCountdown(_timeMillis.value)
                // Duraklatıldığı durumdan hangi modda devam ettiğini koru
                // _pomodoroState.value = _pomodoroState.value // Zaten mevcut değeri koruyor, bu satıra gerek yok.
            }
            // WORKING, SHORT_BREAK, LONG_BREAK durumlarında zaten süre devam ediyor demektir.
            // Bu durumlarda sadece _timeMillis'in mevcut değerinden devam et.
            PomodoroState.WORKING, PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                startCountdown(_timeMillis.value)
            }
        }
    }

    private fun startCountdown(initialTime: Long) {
        timerJob?.cancel() // Önceki zamanlayıcı işini iptal et (varsa)
        _timeMillis.value = initialTime // Başlangıç süresini ayarla

        timerJob = viewModelScope.launch {
            // Eğer durdurulmuş veya duraklatılmış bir durumdan başlatılıyorsa, durumu ayarla.
            // Aksi takdirde, mevcut durumu (çalışma/mola) koru.
            if (_pomodoroState.value == PomodoroState.STOPPED || _pomodoroState.value == PomodoroState.PAUSED) {
                _pomodoroState.value = PomodoroState.WORKING // Varsayılan olarak çalışma durumu
            }

            while (_timeMillis.value > 0 && !isPaused) {
                delay(1000) // Her saniye bekle
                _timeMillis.value -= 1000 // Süreyi azalt
            }

            if (_timeMillis.value <= 0) {
                handleTimerEnd()
                // Süre bittiğinde bildirim gönder!
                val notificationTitle = when (_pomodoroState.value) {
                    PomodoroState.WORKING -> "Mola Zamanı!"
                    PomodoroState.SHORT_BREAK -> "Çalışma Zamanı!"
                    PomodoroState.LONG_BREAK -> "Çalışma Zamanı!"
                    else -> "Pomodoro Tamamlandı!"
                }
                // Bildirim mesajını güncel ayarlara göre dinamikleştir
                val notificationMessage = when (_pomodoroState.value) {
                    PomodoroState.WORKING -> "Kısa bir mola verelim. ${initialShortBreakTimeMillis / (60 * 1000)} dakikalık molana başla."
                    PomodoroState.SHORT_BREAK -> "Mola bitti. Yeni bir Pomodoro turuna başla!"
                    PomodoroState.LONG_BREAK -> "Uzun molan bitti. Yeni bir Pomodoro turuna başla!"
                    else -> "Geri sayım bitti."
                }
                NotificationHelper.showNotification(application.applicationContext, notificationTitle, notificationMessage)
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
        _timeMillis.value = initialWorkTimeMillis // Ayarlanmış başlangıç çalışma süresine dön
        _pomodoroState.value = PomodoroState.STOPPED // Durumu STOPPED olarak güncelle
        _currentRound.value = 1 // Tur sayısını sıfırla
        isPaused = false // Duraklatma durumunu sıfırla
    }

    // --- Zamanlayıcı Süresi Bittiğinde Yapılacaklar ---

    private fun handleTimerEnd() {
        when (_pomodoroState.value) {
            PomodoroState.WORKING -> {
                _currentRound.value++ // Tur sayısını artır
                // Long break interval'ı güncel ayardan al
                if ((_currentRound.value - 1) % currentLongBreakInterval == 0 && _currentRound.value > 1) {
                    // Uzun mola zamanı
                    _timeMillis.value = initialLongBreakTimeMillis
                    _pomodoroState.value = PomodoroState.LONG_BREAK
                } else {
                    // Kısa mola zamanı
                    _timeMillis.value = initialShortBreakTimeMillis
                    _pomodoroState.value = PomodoroState.SHORT_BREAK
                }
                // Bir sonraki fazı otomatik başlat (eğer duraklatma veya durdurma yoksa)
                startCountdown(_timeMillis.value)
            }
            PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                // Mola bitti, çalışma moduna dön
                _timeMillis.value = initialWorkTimeMillis
                _pomodoroState.value = PomodoroState.WORKING
                // Bir sonraki fazı otomatik başlat
                startCountdown(_timeMillis.value)
            }
            PomodoroState.PAUSED, PomodoroState.STOPPED -> {
                // Bu durumlar timerEnd'de normalde çağrılmaz (timerJob cancel edildiği için)
                // Ama eğer çağrılırsa burada bir şey yapmaya gerek yok.
            }
        }
    }

    // ViewModel temizlendiğinde çalışan coroutine'leri ve kaynakları serbest bırak.
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        // SoundPlayer'ı temizlemek istersen burada stopSound() çağırabilirsin
        // soundPlayer.stopSound() // Eğer kullanıyorsan
    }

    // --- Sabitler ---
    // Bu sabitler artık SettingsManager'dan alınacak, o yüzden burada tutulmalarına gerek yok
    // Ancak, geri dönüş (stopTimer) veya varsayılan değerler için hala kullanılabilirler.
    // Şimdilik silmiyorum ama gelecekte kaldırılabilirler.
    companion object {
        const val DEFAULT_WORK_TIME_MILLIS = 25 * 60 * 1000L // 25 dakika
        const val DEFAULT_SHORT_BREAK_TIME_MILLIS = 5 * 60 * 1000L // 5 dakika
        const val DEFAULT_LONG_BREAK_TIME_MILLIS = 15 * 60 * 1000L // 15 dakika
        private const val LONG_BREAK_INTERVAL = 4 // Her 4 çalışma turunda bir uzun mola
    }
}