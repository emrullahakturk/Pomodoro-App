package com.yargisoft.pomodoroapp.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yargisoft.pomodoroapp.ui.home.PomodoroState

@Composable
fun ControlButtons(
    pomodoroState: PomodoroState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (pomodoroState == PomodoroState.STOPPED || pomodoroState == PomodoroState.PAUSED) {
            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("BAŞLAT", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Button(
                onClick = onPauseClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("DURAKLAT", style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onStopClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("SIFIRLA", style = MaterialTheme.typography.titleMedium)
        }
    }
}