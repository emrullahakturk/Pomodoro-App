// app/src/main/java/com/seninuygulamaninadi/ui/settings/SettingsScreen.kt
package com.yargisoft.pomodoroapp.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yargisoft.pomodoroapp.ui.theme.PomodoroAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val workTime by viewModel.workTime.collectAsState()
    val shortBreakTime by viewModel.shortBreakTime.collectAsState()
    val longBreakTime by viewModel.longBreakTime.collectAsState()
    val longBreakInterval by viewModel.longBreakInterval.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                SettingTextField(
                    label = "Çalışma Süresi (dakika)",
                    value = workTime.toString(),
                    onValueChange = { newValue ->
                        viewModel.updateWorkTime(newValue.toIntOrNull() ?: workTime)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                SettingTextField(
                    label = "Kısa Mola Süresi (dakika)",
                    value = shortBreakTime.toString(),
                    onValueChange = { newValue ->
                        viewModel.updateShortBreakTime(newValue.toIntOrNull() ?: shortBreakTime)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                SettingTextField(
                    label = "Uzun Mola Süresi (dakika)",
                    value = longBreakTime.toString(),
                    onValueChange = { newValue ->
                        viewModel.updateLongBreakTime(newValue.toIntOrNull() ?: longBreakTime)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                SettingTextField(
                    label = "Uzun Mola Aralığı (tur)",
                    value = longBreakInterval.toString(),
                    onValueChange = { newValue ->
                        viewModel.updateLongBreakInterval(newValue.toIntOrNull() ?: longBreakInterval)
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Kullanıcı ayarları kaydetmek için bir buton ekleyebiliriz
                // Ancak DataStore'a yazma anında olduğu için bu butona gerek kalmıyor
                // Button(onClick = { /* Ayarlar otomatik kaydedildi */ }) {
                //     Text("Ayarları Kaydet")
                // }
            }
        }
    }
}

@Composable
fun SettingTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            // Sadece sayısal girişlere izin ver
            if (newText.all { it.isDigit() } || newText.isEmpty()) {
                onValueChange(newText)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    PomodoroAppTheme {
        SettingsScreen(navController = rememberNavController())
    }
}