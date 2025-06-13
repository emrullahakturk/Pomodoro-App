package com.yargisoft.pomodoroapp.ui.home



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yargisoft.pomodoroapp.ui.components.ControlButtons
import com.yargisoft.pomodoroapp.ui.components.TimerDisplay
import com.yargisoft.pomodoroapp.ui.theme.PomodoroAppTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PomodoroViewModel = hiltViewModel()
) {
    val timeMillis by viewModel.timeMillis.collectAsState()
    val pomodoroState by viewModel.pomodoroState.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()

    // ViewModel'dan toplam süre bilgisini al (varsayılan olarak çalışma süresi)
    val totalTimeForProgress = when (pomodoroState) {
        PomodoroState.WORKING -> PomodoroViewModel.DEFAULT_WORK_TIME_MILLIS
        PomodoroState.SHORT_BREAK -> PomodoroViewModel.DEFAULT_SHORT_BREAK_TIME_MILLIS
        PomodoroState.LONG_BREAK -> PomodoroViewModel.DEFAULT_LONG_BREAK_TIME_MILLIS
        else -> PomodoroViewModel.DEFAULT_WORK_TIME_MILLIS // Diğer durumlarda varsayılan
    }

    // Temayı Pomodoro durumuna göre dinamikleştiriyoruz
    PomodoroAppTheme(pomodoroState = pomodoroState) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when (pomodoroState) {
                        PomodoroState.WORKING -> "ÇALIŞMA ZAMANI"
                        PomodoroState.SHORT_BREAK -> "KISA MOLA"
                        PomodoroState.LONG_BREAK -> "UZUN MOLA"
                        PomodoroState.PAUSED -> "DURAKLATILDI"
                        PomodoroState.STOPPED -> "HAZIR"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Zamanlayıcı ve ilerleme çubuğunu gösteren yeni Composable
                TimerDisplay(
                    timeMillis = timeMillis,
                    pomodoroState = pomodoroState,
                    totalTimeMillis = totalTimeForProgress // İlerleme için toplam süreyi ilet
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Tur: $currentRound",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Kontrol düğmeleri için yeni Composable
                ControlButtons(
                    pomodoroState = pomodoroState,
                    onStartClick = { viewModel.startTimer() },
                    onPauseClick = { viewModel.pauseTimer() },
                    onStopClick = { viewModel.stopTimer() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    PomodoroAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}