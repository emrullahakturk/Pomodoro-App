package com.yargisoft.pomodoroapp.ui.navigation
// app/src/main/java/com/seninuygulamaninadi/ui/navigation/AppNavHost.kt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yargisoft.pomodoroapp.ui.home.HomeScreen

// Uygulamanın navigasyon grafiğini tanımlayan Composable fonksiyon.
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(), // NavController'ı yönetir
    startDestination: String = Screen.Home.route // Uygulama başladığında gösterilecek ekran
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // "home_screen" rotasına ulaşıldığında HomeScreen Composable'ını göster.
        composable(Screen.Home.route) {
            HomeScreen(navController = navController) // HomeScreen'e navController'ı iletelim
        }

        // İleride eklenecek diğer ekranlar için:
        // composable(Screen.Settings.route) {
        //     SettingsScreen(navController = navController)
        // }
    }
}