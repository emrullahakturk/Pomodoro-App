package com.yargisoft.pomodoroapp
// app/src/main/java/com/seninuygulamaninadi/MainActivity.kt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.yargisoft.pomodoroapp.ui.navigation.AppNavHost
import com.yargisoft.pomodoroapp.ui.theme.PomodoroAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AppNavHost'u çağırarak uygulamanın navigasyonunu başlatıyoruz
                    AppNavHost()
                }
            }
        }
    }
}