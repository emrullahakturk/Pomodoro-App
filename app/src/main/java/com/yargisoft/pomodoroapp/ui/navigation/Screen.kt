package com.yargisoft.pomodoroapp.ui.navigation


// Uygulamamızdaki her ekranı temsil eden sealed class.
// Her bir object, bir navigasyon rotasını (string path) içerir.
sealed class Screen(val route: String) {
    object Home : Screen("home_screen") // Ana Pomodoro ekranı
    object Settings : Screen("settings_screen") // İleride eklenecek ayarlar ekranı
    // object Statistics : Screen("statistics_screen") // İleride eklenecek istatistik ekranı
}