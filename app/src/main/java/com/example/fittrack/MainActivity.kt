package com.example.fittrack

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.fittrack.database.TrackFitDatabase
import com.example.fittrack.service.Service
import com.example.fittrack.ui.screens.*
import com.example.fittrack.ui.theme.FitTrackTheme
import com.example.fittrack.ui.theme_config.ThemePreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var database: TrackFitDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            TrackFitDatabase::class.java,
            "trackfit-db"
        )
            .fallbackToDestructiveMigration(false)
            .build()

        lifecycleScope.launch {
            //Service.insertLogsFromApi()
            //Service.insertNotesFromApi()
            //Service.insertRoutinesFromApi()
            Service.insertExercisesFromApi()
            //Service.insertTargetLocationsFromApi()
            Service.insertUserFromApi()
        }

        enableEdgeToEdge()

        setContent {
            val context = this@MainActivity
            val prefs = remember { ThemePreferences(context) }
            val scope = rememberCoroutineScope()
            val isDarkTheme by prefs.darkModeFlow.collectAsState(initial = false)

            FitTrackTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val dao = database.trackFitDao()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("profile") {
                            ProfileScreen(navController)
                        }
                        composable("settings") {
                            SettingsScreen(
                                navController = navController,
                                darkTheme = isDarkTheme,
                                onThemeToggle = {
                                    scope.launch { prefs.saveDarkMode(it) }
                                }
                            )
                        }
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("user_data") {
                            UserDataScreen(navController)
                        }
                        composable("routine/{routineId}") { backStackEntry ->
                            val id = backStackEntry.arguments
                                ?.getString("routineId")
                                ?.toIntOrNull() ?: return@composable
                            RoutinePage(navController, id)
                        }
                        composable("exercise_list") {
                            ExerciseListPage(navController)
                        }
                        composable("exercise_logs/{exerciseId}") { backStackEntry ->
                            val id = backStackEntry.arguments
                                ?.getString("exerciseId")
                                ?.toIntOrNull() ?: return@composable
                            ExerciseLogsPage(
                                exerciseId = id,
                                navController = navController,
                                dao = dao
                            )
                        }
                        composable("notes") {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                NotesScreen(navController, dao)
                            }
                        }
                        composable("create_routine") {
                            CreateRoutinePage(navController)
                        }
                        composable("map") {
                            MapPage(navController)
                        }
                    }
                }
            }
        }
    }

}
