package com.yargisoft.pomodoroapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview // Preview için bu import gerekli
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // Preview için bu import gerekli
import com.yargisoft.pomodoroapp.ui.components.ControlButtons
import com.yargisoft.pomodoroapp.ui.components.TimerDisplay
import com.yargisoft.pomodoroapp.ui.navigation.Screen
import com.yargisoft.pomodoroapp.ui.theme.PomodoroAppTheme

@OptIn(ExperimentalMaterial3Api::class) // TopAppBar için
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PomodoroViewModel = hiltViewModel()
) {
    val timeMillis by viewModel.timeMillis.collectAsState()
    val pomodoroState by viewModel.pomodoroState.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()

    // BURADAKİ HESAPLAMAYI DÜZELTİYORUZ
    val totalTimeForProgress = when (pomodoroState) {
        PomodoroState.WORKING -> viewModel.initialWorkTimeMillis
        PomodoroState.SHORT_BREAK -> viewModel.initialShortBreakTimeMillis
        PomodoroState.LONG_BREAK -> viewModel.initialLongBreakTimeMillis
        // Duraklatılmış veya Durdurulmuş durumlar için:
        // Eğer duraklatılmışsa, mevcut kalan süre hangi aralıktaysa o modun başlangıç süresini kullan.
        // Eğer durdurulmuşsa (başlangıç durumu), çalışma süresinin başlangıcını kullan.
        PomodoroState.PAUSED, PomodoroState.STOPPED -> {
            val currentTime = timeMillis
            when {
                currentTime > viewModel.initialLongBreakTimeMillis -> viewModel.initialWorkTimeMillis
                currentTime > viewModel.initialShortBreakTimeMillis -> viewModel.initialShortBreakTimeMillis
                else -> viewModel.initialLongBreakTimeMillis // Eğer kısa moladan daha az süre kalmışsa uzun mola süresi varsayılabilir,
                // veya daha spesifik bir mantık gerekebilir.
                // En güvenlisi başlangıç çalışma süresini göstermektir.
            }
        }
    }


    PomodoroAppTheme(pomodoroState = pomodoroState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pomodoro Zamanlayıcı") },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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

                    TimerDisplay(
                        timeMillis = timeMillis,
                        pomodoroState = pomodoroState,
                        totalTimeMillis = totalTimeForProgress // BURASI ARTIK DOĞRU
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Tur: $currentRound",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(32.dp))

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
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    PomodoroAppTheme {
        // Preview için sahte bir NavController kullanabiliriz
        HomeScreen(navController = rememberNavController())
    }
}