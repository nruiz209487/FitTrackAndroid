package com.example.fittrack.service

import com.example.fittrack.MainActivity
import com.example.fittrack.api.ApiClient
import com.example.fittrack.entity.UserEntity

object ApiRequest {

    suspend fun insertLogsFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiLogs = ApiClient.getExerciseLogs(userId)
            dao.insertExerciseLogs(apiLogs)
        }
    }

    suspend fun insertNotesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiNotes = ApiClient.getNotes(userId)
            dao.insertNotes(apiNotes)
        }
    }

    suspend fun insertRoutinesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiRoutines = ApiClient.getRoutines(userId)
            dao.insertRoutines(apiRoutines)
        }
    }

    suspend fun insertExercisesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiExercises = ApiClient.getExercises()
        dao.insertExercies(apiExercises)
    }

    suspend fun insertTargetLocationsFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiTargetLocations = ApiClient.getTargetLocations()
        dao.insertTargetLocations(apiTargetLocations)
    }

    suspend fun insertUserFromApi() {
        val dao = MainActivity.database.trackFitDao()
        TokenManager.userId?.let { userId ->
            val apiUser = ApiClient.getUser(userId)
            apiUser?.let {
                dao.insertUser(it)
            }
        }
    }
    suspend fun insertUserToApi(userEntity: UserEntity) {
        val apiTargetLocations = ApiClient.getTargetLocations()
    }
    suspend fun updateUserApi(userEntity: UserEntity) {
        val dao = MainActivity.database.trackFitDao()
        dao.updateUser(userEntity)
    }
}
