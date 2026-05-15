package com.example.kavyakanaja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kavyakanaja.repository.PoemRepository
import com.example.kavyakanaja.ui.navigation.Screen
import com.example.kavyakanaja.ui.screens.*
import com.example.kavyakanaja.ui.theme.KavyaKanajaTheme
import com.example.kavyakanaja.viewmodel.PoemViewModel
import com.example.kavyakanaja.viewmodel.PoemViewModelFactory

import com.example.kavyakanaja.repository.PoetRepository
import com.example.kavyakanaja.viewmodel.PoetViewModel

import com.example.kavyakanaja.utils.AudioPlayer
import com.example.kavyakanaja.viewmodel.AudioViewModel

import com.example.kavyakanaja.repository.UserRepository
import com.example.kavyakanaja.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val poemRepository = PoemRepository(this)
        val poetRepository = PoetRepository(this)
        val audioPlayer = AudioPlayer(this)
        val userRepository = UserRepository(this)
        val factory = PoemViewModelFactory(poemRepository, poetRepository, audioPlayer, userRepository)

        setContent {
            MainApp(factory)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(factory: PoemViewModelFactory) {
    val navController = rememberNavController()
    val poemViewModel: PoemViewModel = viewModel(factory = factory)
    val audioViewModel: AudioViewModel = viewModel(factory = factory)
    val userViewModel: UserViewModel = viewModel(factory = factory)
    val poetViewModel: PoetViewModel = viewModel(factory = factory)

    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    val language by userViewModel.language.collectAsState()
    val themeMode by userViewModel.themeMode.collectAsState()
    val darkTheme = when (themeMode) {
        UserRepository.THEME_DARK -> true
        UserRepository.THEME_LIGHT -> false
        UserRepository.THEME_SYSTEM -> isSystemInDarkTheme()
        else -> isSystemInDarkTheme()
    }

    LaunchedEffect(language) {
        poemViewModel.loadPoems(language)
    }

    KavyaKanajaTheme(darkTheme = darkTheme) {
        MainAppScaffold(
            navController = navController,
            poemViewModel = poemViewModel,
            audioViewModel = audioViewModel,
            userViewModel = userViewModel,
            poetViewModel = poetViewModel,
            isLoggedIn = isLoggedIn
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScaffold(
    navController: NavHostController,
    poemViewModel: PoemViewModel,
    audioViewModel: AudioViewModel,
    userViewModel: UserViewModel,
    poetViewModel: PoetViewModel,
    isLoggedIn: Boolean
) {
    val items = listOf(
        Screen.Home,
        Screen.Explore,
        Screen.Profile,
        Screen.Settings
    )

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentScreen = items.find { it.route == currentRoute }
            
            if (isLoggedIn && currentScreen != null) {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            when (currentScreen) {
                                Screen.Home -> "Kavya Kanaja"
                                Screen.Explore -> "Explore Poets"
                                Screen.Profile -> "My Profile"
                                Screen.Settings -> "Settings"
                                else -> ""
                            }
                        )
                    }
                )
            }
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = items.any { it.route == currentRoute }

            if (isLoggedIn && showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    when (screen) {
                                        Screen.Home -> Icons.Default.Home
                                        Screen.Explore -> Icons.AutoMirrored.Filled.List
                                        Screen.Profile -> Icons.Default.Person
                                        Screen.Settings -> Icons.Default.Settings
                                        else -> Icons.Default.Home
                                    },
                                    contentDescription = screen.route
                                )
                            },
                            label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(userViewModel) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
            composable(Screen.Home.route) {
                HomeScreen(poemViewModel) { poemId ->
                    navController.navigate(Screen.PoemDetail.createRoute(poemId))
                }
            }
            composable(Screen.Explore.route) {
                PoetsScreen(poetViewModel) { poetId ->
                    navController.navigate(Screen.PoetDetail.createRoute(poetId))
                }
            }
            composable(
                route = Screen.PoetDetail.route,
                arguments = listOf(navArgument("poetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val poetId = backStackEntry.arguments?.getString("poetId")
                val poets by poetViewModel.poets.collectAsState()
                val poet = poets.find { it.id == poetId }
                PoetDetailScreen(poet, userViewModel) {
                    navController.popBackStack()
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    userViewModel = userViewModel,
                    poemViewModel = poemViewModel,
                    poetViewModel = poetViewModel,
                    onFavoritePoemClick = { poemId ->
                        navController.navigate(Screen.PoemDetail.createRoute(poemId))
                    },
                    onFavoritePoetClick = { poetId ->
                        navController.navigate(Screen.PoetDetail.createRoute(poetId))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(userViewModel)
            }
            composable(
                route = Screen.PoemDetail.route,
                arguments = listOf(navArgument("poemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val poemId = backStackEntry.arguments?.getString("poemId")
                val poems by poemViewModel.poems.collectAsState()
                val poem = poems.find { it.id == poemId }
                PoemDetailScreen(poem, audioViewModel, userViewModel) {
                    navController.popBackStack()
                }
            }
        }
    }
}
