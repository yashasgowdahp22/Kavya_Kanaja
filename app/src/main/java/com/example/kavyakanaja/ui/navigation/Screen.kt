package com.example.kavyakanaja.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Poets : Screen("poets")
    object PoetDetail : Screen("poet_detail/{poetId}") {
        fun createRoute(poetId: String) = "poet_detail/$poetId"
    }
    object PoemDetail : Screen("poem_detail/{poemId}") {
        fun createRoute(poemId: String) = "poem_detail/$poemId"
    }
}
