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
import com.example.fittrack.api.ApiClient
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
            insertLogsFromApi()
            insertNotesFromApi()
            inserRoutinesFromApi()
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
                        composable("notes") {
                            NotesScreen(navController, dao)
                        }
                        composable("map") {
                            MapPage(navController)
                        }
                    }
                }
            }
        }
    }

    private suspend fun insertLogsFromApi() {
        val dao = database.trackFitDao()
        val apiLogs = ApiClient.getExerciseLogs()
        dao.insertExerciseLogs(apiLogs)
    }
    private suspend fun insertNotesFromApi() {
        val dao = database.trackFitDao()
        val apiNotes = ApiClient.getNotes()
        dao.insertNotes(apiNotes)
    }
    private suspend fun inserRoutinesFromApi() {
        val dao = database.trackFitDao()
        val apiRoutes = ApiClient.getRoutines()
        dao.insertRoutines(apiRoutes)
    }
}