package com.example.fittrack.service

import com.example.fittrack.MainActivity
import com.example.fittrack.api.ApiClient
import com.example.fittrack.entity.UserEntity

object ApiRequest {
    suspend fun insertLogsFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiLogs = ApiClient.getExerciseLogs(1)
        dao.insertExerciseLogs(apiLogs)
    }

    suspend fun insertNotesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiNotes = ApiClient.getNotes(1)
        dao.insertNotes(apiNotes)
    }

    suspend fun insertRoutinesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiRoutines = ApiClient.getRoutines(1)
        dao.insertRoutines(apiRoutines)
    }

    suspend fun insertExercisesFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiExercises = ApiClient.getExercises()
        dao.insertExercies(apiExercises)
    }
    suspend fun insertTargetLocationsFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiTargetLocations= ApiClient.getTargetLocations()
        dao.insertTargetLocations(apiTargetLocations)
    }

    suspend fun insertUserFromApi() {
        val dao = MainActivity.database.trackFitDao()
        val apiUser =ApiClient.getUser()
        dao.insertUser(apiUser)
    }

    suspend fun updateUserApi(userEntity: UserEntity) {
        val dao = MainActivity.database.trackFitDao()

            dao.updateUser(userEntity)
        }
    }



