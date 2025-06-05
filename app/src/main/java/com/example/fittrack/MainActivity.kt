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
import com.example.fittrack.entity.ExerciseLogEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.TargetLocationEntity
import com.example.fittrack.entity.UserEntity
import com.example.fittrack.service.Service
import com.example.fittrack.ui.screens.*
import com.example.fittrack.ui.theme.FitTrackTheme
import com.example.fittrack.ui.theme_config.ThemePreferences
import com.google.android.gms.maps.model.LatLng
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
            "trackfit-db")
            .fallbackToDestructiveMigration(false)
            .build()
        val testUser = UserEntity(
            name = "Test User",
            email = "testuser_${System.currentTimeMillis()}@example.com",
            streakDays = 1,
            profileImage = "https://example.com/avatar.png",
            lastStreakDay = "2025-06-05",
            password = "password123",
        )
        val testRoutine = RoutineEntity(
            name = "Rutina de prueba",
            description = "Descripción de la rutina de prueba",
            imageUri = "https://example.com/routine_image.png",
            exerciseIds = "1,2,3", // IDs de ejercicios separados por coma
            userId = testUser.id // o el campo que uses como clave externa
        )
        val testNote = NoteEntity(
            header = "Rutina de prueba",
            text = "Descripción de la rutina de prueba",
            timestamp = "2025-06-05T21:26:12.000Z", // ← Fecha válida
            userId = testUser.id // o el campo que uses como clave externa
        )
        val testExerciseLog = ExerciseLogEntity(
            exerciseId = 1,
            date = "2025-06-05T21:26:12.000Z", // ← Fecha válida
            weight = 23.0f,
            reps = 1, // ← Fecha válida
            userId = testUser.id // o el campo que uses como clave externa
        )
        val targetLocationEntity = TargetLocationEntity(
            name = "hola",
            position = LatLng(19.4326, -99.1332), // ejemplo válido
            radiusMeters = 200.0
        )

        val dao = database.trackFitDao()

        lifecycleScope.launch {
           //    Service.insertLogsFromApi()
         //    Service.insertNotesFromApi()
         //   Service.insertRoutinesFromApi()
        //     Service.insertExercisesFromApi()
        //    Service.insertTargetLocationsFromApi()
         //    Service.insertUserToApi(testUser)
         //   Service.login(testUser)
         //    Service.insertRoutineToApi(testRoutine,8)
      //      Service.insertNoteToApi(testNote,8)
           // Service.insertExerciseLogToApi(testExerciseLog,8)
       //     Service.deleteRoutine( 1,8)
           //  Service.deleteNote(1,8)
           // Service.deletetExerciseLog(1,8)
            Service.insertTargetLocationsToApi(targetLocationEntity,8)
                dao.insertUser(testUser)
        }

        enableEdgeToEdge()

        setContent {
            val context = this@MainActivity
            val prefs = remember { ThemePreferences(context) }
            val scope = rememberCoroutineScope()
            val isDarkTheme by prefs.darkModeFlow.collectAsState(initial = false)

            FitTrackTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
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
                        composable("IMCScreen") {
                            IMCScreen(navController)
                        }
                        composable("map") {
                            MapPage(navController)
                        }
                        composable("createNewTargetLocation") {
                            CreateNewTargetLocation(navController)
                        }
                        composable("targetLocation") {
                            TargetLocationsScreen(navController)
                        }

                    }
                }
            }
        }
    }
}
