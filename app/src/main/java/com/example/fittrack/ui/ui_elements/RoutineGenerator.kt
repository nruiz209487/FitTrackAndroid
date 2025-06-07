package com.example.fittrack.ui.ui_elements

import com.example.fittrack.MainActivity
import com.example.fittrack.entity.RoutineEntity
class RoutineGenerator {

    // Datos de los ejercicios organizados por categorías
    private val exercisesByCategory = mapOf(
        "fuerza_basica" to listOf(1, 3, 5, 10, 12, 15, 17), // Flexiones, Press banca, Peso muerto, Dominadas, Sentadillas, Press militar, Curl bíceps
        "cardio_bajo" to listOf(21, 29, 41, 61), // Skipping, Jumping Jacks, Jump Rope, Sprint cinta
        "cardio_intenso" to listOf(13, 14, 18, 51, 71), // Burpees, Mountain Climbers, Jump Squats, Jump Lunges, Battle Ropes
        "core_basico" to listOf(2, 7, 8, 9, 16), // Planchas, Crunch, Elevación piernas, Oblicuos, Russian Twists
        "core_avanzado" to listOf(30, 34, 40, 56, 65), // Ab Wheel, Dragon Flags, Plancha lateral, Tijeras abdominales, Elevación rodillas colgado
        "piernas" to listOf(6, 20, 26, 37, 47, 64), // Zancadas, Hip Thrust, Patada glúteo, Step-ups, Sentadilla sumo, Sentadillas búlgaras
        "espalda" to listOf(4, 25, 28, 36, 45, 63), // Remo mancuernas, Remo barra, Face Pulls, Dorsales polea, Remo máquina, Remo invertido
        "pecho" to listOf(32, 38, 44, 62, 73, 85), // Flexiones diamante, Flexiones palmada, Press inclinado, Press agarre cerrado, Press mancuernas, Flexiones explosivas
        "hombros" to listOf(24, 35, 50, 97, 114), // Press Arnold, Press hombros mancuernas, Elevaciones laterales, Press hombros barra, Press hombros sentado
        "brazos" to listOf(11, 22, 46, 89, 109), // Fondos tríceps, Curl tríceps, Curl martillo, Curl inclinado, Fondos agarre estrecho
        "funcional" to listOf(39, 49, 66, 68, 76, 88), // Kettlebell Swings, Paseo granjero, Clean Press, Martillo neumático, Snatch mancuerna, Swing una mano
        "maquinas" to listOf(33, 54, 104, 106), // Prensa piernas, Prensa inclinada, Extensiones cuádriceps, Elevación gemelos máquina
        "bajo_impacto" to listOf(42, 53, 57, 82, 115, 119) // Band Pull Aparts, Face Pulls cuerda, Extensiones espalda, Press banda, Elevación piernas banda, Sentadilla banda
    )

    // Función principal para generar rutinas según IMC
    fun generateWeeklyRoutines(imc: Double, genero: String, userId: Int): List<RoutineEntity> {
        return when {
            imc < 18.5 -> generateRoutinesForUnderweight(userId)
            imc < 25 -> generateRoutinesForNormal(userId)
            imc < 30 -> generateRoutinesForOverweight(userId)
            else -> generateRoutinesForObese(userId)
        }
    }

    // Rutinas para peso bajo - Enfoque en ganar masa muscular
    private fun generateRoutinesForUnderweight(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - Pecho y Tríceps",
                description = "Rutina de fuerza para ganar masa muscular en tren superior",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["fuerza_basica"]!!.take(2),
                    exercisesByCategory["pecho"]!!.take(2),
                    exercisesByCategory["brazos"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Martes - Piernas y Glúteos",
                description = "Desarrollo de masa muscular en tren inferior",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(5, 12), // Peso muerto, Sentadillas
                    exercisesByCategory["piernas"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - Espalda y Bíceps",
                description = "Fortalecimiento de espalda y brazos",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(10), // Dominadas
                    exercisesByCategory["espalda"]!!.take(3),
                    exercisesByCategory["brazos"]!!.filter { it in listOf(17, 46) }
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Descanso Activo",
                description = "Cardio ligero y movilidad",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - Hombros y Core",
                description = "Desarrollo de hombros y fortalecimiento del core",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["hombros"]!!.take(3),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Full Body",
                description = "Rutina completa de cuerpo entero",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(1, 12, 4, 15), // Flexiones, Sentadillas, Remo, Press militar
                    exercisesByCategory["funcional"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Descanso",
                description = "Estiramientos y movilidad",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }

    // Rutinas para peso normal - Mantenimiento equilibrado
    private fun generateRoutinesForNormal(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - Fuerza Upper",
                description = "Entrenamiento de fuerza para tren superior",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["fuerza_basica"]!!.take(2),
                    exercisesByCategory["pecho"]!!.take(1),
                    exercisesByCategory["espalda"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Martes - Cardio HIIT",
                description = "Cardio de alta intensidad",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_intenso"]!!.take(3),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - Fuerza Lower",
                description = "Entrenamiento de piernas y glúteos",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(5, 12), // Peso muerto, Sentadillas
                    exercisesByCategory["piernas"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Cardio Moderado",
                description = "Cardio de intensidad moderada",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["funcional"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - Push/Pull",
                description = "Rutina de empuje y tracción",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["pecho"]!!.take(2),
                    exercisesByCategory["hombros"]!!.take(1),
                    exercisesByCategory["espalda"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Funcional",
                description = "Entrenamiento funcional completo",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["funcional"]!!.take(3),
                    exercisesByCategory["core_avanzado"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Movilidad",
                description = "Recuperación y estiramientos",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }

    // Rutinas para sobrepeso - Enfoque en pérdida de grasa
    private fun generateRoutinesForOverweight(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - HIIT Total Body",
                description = "Rutina de alta intensidad para quemar grasa",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_intenso"]!!.take(3),
                    exercisesByCategory["funcional"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Martes - Fuerza + Cardio",
                description = "Combinación de fuerza y cardio",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["fuerza_basica"]!!.take(2),
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_basico"]!!.take(1)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - HIIT Inferior",
                description = "HIIT enfocado en tren inferior",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(18, 51), // Jump Squats, Jump Lunges
                    exercisesByCategory["piernas"]!!.take(2),
                    exercisesByCategory["cardio_intenso"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Circuito Metabólico",
                description = "Circuito para acelerar metabolismo",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["funcional"]!!.take(3),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - HIIT Superior",
                description = "HIIT para tren superior",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(13, 14), // Burpees, Mountain Climbers
                    exercisesByCategory["pecho"]!!.take(1),
                    exercisesByCategory["cardio_intenso"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Cardio + Core",
                description = "Cardio intenso con trabajo de core",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_avanzado"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Recuperación Activa",
                description = "Movimiento suave para recuperación",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }

    // Rutinas para obesidad - Bajo impacto, progresivo
    private fun generateRoutinesForObese(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - Inicio Suave",
                description = "Rutina de bajo impacto para comenzar",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3),
                    exercisesByCategory["maquinas"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Martes - Cardio Ligero",
                description = "Cardio de baja intensidad",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - Fuerza Máquinas",
                description = "Fortalecimiento con máquinas",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["maquinas"]!!,
                    exercisesByCategory["bajo_impacto"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Movilidad",
                description = "Mejora de flexibilidad y movilidad",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(4)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - Funcional Básico",
                description = "Movimientos funcionales básicos",
                imageUri = "",
                exerciseIds = combineExercises(
                    listOf(1, 12), // Flexiones modificadas, Sentadillas
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Cardio + Fuerza",
                description = "Combinación suave de cardio y fuerza",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["maquinas"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Descanso Activo",
                description = "Movimiento muy suave y estiramientos",
                imageUri = "",
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }

    // Función auxiliar para combinar ejercicios
    private fun combineExercises(vararg exerciseLists: List<Int>): String {
        return exerciseLists.flatMap { it }.joinToString(",")
    }
}

// Haz esta función suspend para poder llamar a funciones suspend dentro
suspend fun generateAndSaveRoutines(imc: Double, genero: String, userId: Int) {
    val routineGenerator = RoutineGenerator()
    val weeklyRoutines = routineGenerator.generateWeeklyRoutines(imc, genero, userId)

    // Guardar rutinas en la base de datos dentro de la corutina
    val dao = MainActivity.database.trackFitDao()
    weeklyRoutines.forEach { routine ->
        dao.insertRoutine(routine)
    }
}


