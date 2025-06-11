package com.example.fittrack.ui.helpers
import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.service.Service
import com.example.fittrack.MainActivity

class RoutineGenerator {
    /**
     * Genera las rutinas y las guarda en la db
     */
    companion object {
        suspend fun generateAndSaveRoutines(imc: Double,userId: Int) {
            val routineGenerator = RoutineGenerator()
            val weeklyRoutines = routineGenerator.generateWeeklyRoutines(imc,userId) // pasa el imc para selecionar rutinas

            weeklyRoutines.forEach { routine ->
                Service.insertRoutineToApi(routine)
            }
        }
    }

    //Listados de ejercicios
    private val exercisesByCategory = mapOf(
        "fuerza_basica" to listOf(1, 3, 5, 10, 12, 15, 17),
        "cardio_bajo" to listOf(21, 29, 41, 61),
        "cardio_intenso" to listOf(13, 14, 18, 51, 71),
        "core_basico" to listOf(2, 7, 8, 9, 16),
        "core_avanzado" to listOf(30, 34, 40, 56, 65),
        "piernas" to listOf(6, 20, 26, 37, 47, 64),
        "espalda" to listOf(4, 25, 28, 36, 45, 63),
        "pecho" to listOf(32, 38, 44, 62, 73, 85),
        "hombros" to listOf(24, 35, 50, 97, 114),
        "brazos" to listOf(11, 22, 46, 89, 109),
        "funcional" to listOf(39, 49, 66, 68, 76, 88),
        "maquinas" to listOf(33, 54, 104, 106),
        "bajo_impacto" to listOf(42, 53, 57, 82, 115, 119)
    )

    /**
     * Llama a x funcion dependiendo del ic
     */
    suspend fun generateWeeklyRoutines(imc: Double, userId: Int): List<RoutineEntity> {
        return when {
            imc < 18.5 -> generateRoutinesForUnderweight(userId)
            imc < 25 -> generateRoutinesForNormal(userId)
            imc < 30 -> generateRoutinesForOverweight(userId)
            else -> generateRoutinesForObese(userId)
        }
    }

    /**
     * Rutinas para peso pluma
     */
    private suspend fun generateRoutinesForUnderweight(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - Pecho y Tríceps",
                description = "Rutina de fuerza para ganar masa muscular en tren superior",
                imageUri = getExerciseImage(1),
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
                imageUri = getExerciseImage(5),
                exerciseIds = combineExercises(
                    listOf(5, 12),
                    exercisesByCategory["piernas"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - Espalda y Bíceps",
                description = "Fortalecimiento de espalda y brazos",
                imageUri = getExerciseImage(10),
                exerciseIds = combineExercises(
                    listOf(10),
                    exercisesByCategory["espalda"]!!.take(3),
                    exercisesByCategory["brazos"]!!.filter { it in listOf(17, 46) }
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Descanso Activo",
                description = "Cardio ligero y movilidad",
                imageUri = getExerciseImage(21),
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - Hombros y Core",
                description = "Desarrollo de hombros y fortalecimiento del core",
                imageUri = getExerciseImage(24),
                exerciseIds = combineExercises(
                    exercisesByCategory["hombros"]!!.take(3),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Full Body",
                description = "Rutina completa de cuerpo entero",
                imageUri = getExerciseImage(1),
                exerciseIds = combineExercises(
                    listOf(1, 12, 4, 15),
                    exercisesByCategory["funcional"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Descanso",
                description = "Estiramientos y movilidad",
                imageUri = getExerciseImage(42),
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }
    /**
     * Rutinas para peso noraml
     */
    private suspend fun generateRoutinesForNormal(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - Fuerza Upper",
                description = "Entrenamiento de fuerza para tren superior",
                imageUri = getExerciseImage(1),
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
                imageUri = getExerciseImage(13),
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_intenso"]!!.take(3),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - Fuerza Lower",
                description = "Entrenamiento de piernas y glúteos",
                imageUri = getExerciseImage(12),
                exerciseIds = combineExercises(
                    listOf(5, 12),
                    exercisesByCategory["piernas"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Cardio Moderado",
                description = "Cardio de intensidad moderada",
                imageUri = getExerciseImage(21),
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["funcional"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - Push/Pull",
                description = "Rutina de empuje y tracción",
                imageUri = getExerciseImage(32),
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
                imageUri = getExerciseImage(39),
                exerciseIds = combineExercises(
                    exercisesByCategory["funcional"]!!.take(3),
                    exercisesByCategory["core_avanzado"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Movilidad",
                description = "Recuperación y estiramientos",
                imageUri = getExerciseImage(42),
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }

    /**
     * Rutinas para sobrpeso
     */
    private suspend fun generateRoutinesForOverweight(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - HIIT Total Body",
                description = "Rutina de alta intensidad para quemar grasa",
                imageUri = getExerciseImage(13), // Burpees
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_intenso"]!!.take(3),
                    exercisesByCategory["funcional"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Martes - Fuerza + Cardio",
                description = "Combinación de fuerza y cardio",
                imageUri = getExerciseImage(1),
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
                imageUri = getExerciseImage(18),
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
                imageUri = getExerciseImage(39),
                exerciseIds = combineExercises(
                    exercisesByCategory["funcional"]!!.take(3),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - HIIT Superior",
                description = "HIIT para tren superior",
                imageUri = getExerciseImage(13),
                exerciseIds = combineExercises(
                    listOf(13, 14),
                    exercisesByCategory["pecho"]!!.take(1),
                    exercisesByCategory["cardio_intenso"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Cardio + Core",
                description = "Cardio intenso con trabajo de core",
                imageUri = getExerciseImage(21),
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_avanzado"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Recuperación Activa",
                description = "Movimiento suave para recuperación",
                imageUri = getExerciseImage(42),
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }
    /**
     * Rutinas para obesidad
     */
    private suspend fun generateRoutinesForObese(userId: Int): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                name = "Lunes - Inicio Suave",
                description = "Rutina de bajo impacto para comenzar",
                imageUri = getExerciseImage(42),
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3),
                    exercisesByCategory["maquinas"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Martes - Cardio Ligero",
                description = "Cardio de baja intensidad",
                imageUri = getExerciseImage(21),
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["core_basico"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Miércoles - Fuerza Máquinas",
                description = "Fortalecimiento con máquinas",
                imageUri = getExerciseImage(33),
                exerciseIds = combineExercises(
                    exercisesByCategory["maquinas"]!!,
                    exercisesByCategory["bajo_impacto"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Jueves - Movilidad",
                description = "Mejora de flexibilidad y movilidad",
                imageUri = getExerciseImage(57),
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(4)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Viernes - Funcional Básico",
                description = "Movimientos funcionales básicos",
                imageUri = getExerciseImage(1),
                exerciseIds = combineExercises(
                    listOf(1, 12),
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Sábado - Cardio + Fuerza",
                description = "Combinación suave de cardio y fuerza",
                imageUri = getExerciseImage(21),
                exerciseIds = combineExercises(
                    exercisesByCategory["cardio_bajo"]!!.take(2),
                    exercisesByCategory["maquinas"]!!.take(2)
                ),
                userId = userId
            ),
            RoutineEntity(
                name = "Domingo - Descanso Activo",
                description = "Movimiento muy suave y estiramientos",
                imageUri = getExerciseImage(42),
                exerciseIds = combineExercises(
                    exercisesByCategory["bajo_impacto"]!!.take(3)
                ),
                userId = userId
            )
        )
    }
    /**
     * combian ejercicios
     */
    private fun combineExercises(vararg exerciseLists: List<Int>): String {
        return exerciseLists.flatMap { it }.joinToString(",")
    }

    /**
     * simplemente coje la imagen de un ejercico para ponersela a al rutina
     */
    private suspend fun getExerciseImage(exerciseId: Int): String {
        return try {
            val dao = MainActivity.database.trackFitDao()
            val exercise = dao.getExerciseById(exerciseId)
            exercise?.imageUri ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}