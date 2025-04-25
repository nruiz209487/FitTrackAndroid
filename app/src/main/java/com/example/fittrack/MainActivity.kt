package com.example.fittrack

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
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.ui.screens.*
import com.example.fittrack.ui.theme.FitTrackTheme
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
            insertSampleLogs()
        }

        enableEdgeToEdge()

        setContent {
            var isDarkThemeEnabled by remember { mutableStateOf(false) }
            FitTrackTheme(darkTheme = isDarkThemeEnabled) {
                val navController = rememberNavController()
                val dao = database.trackFitDao()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
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
                                darkTheme = isDarkThemeEnabled,  // Boolean
                                onThemeToggle = { isDarkThemeEnabled = it }
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
                            RoutinePage(navController, id, dao)
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
                        composable("social") {
                            SocialFeedScreen(navController)
                        }
                        composable("map") {
                            MapPage(navController)
                        }
                    }
                }
            }
        }
    }

    private suspend fun insertSampleLogs() {
        val sampleExerciseId = 1
        val existing = database.trackFitDao().getExerciseLogsById(sampleExerciseId)
        if (existing.isEmpty()) {
            val sampleLogs = listOf(
                ExerciseLogEntity(0, sampleExerciseId, "2025-04-18", 60f, 10),
                ExerciseLogEntity(0, sampleExerciseId, "2025-04-20", 62.5f, 8),
                ExerciseLogEntity(0, sampleExerciseId, "2025-04-25", 65f, 7)
            )
            database.trackFitDao().insertExerciseLogs(sampleLogs)
        }
    }
}
