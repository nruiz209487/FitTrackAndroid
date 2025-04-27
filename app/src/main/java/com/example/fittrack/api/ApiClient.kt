package com.example.fittrack.api

import com.example.fittrack.entity.RoutineEntity
import com.example.fittrack.entity.ExerciseEntity
import com.example.fittrack.entity.NoteEntity
import com.example.fittrack.entity.UserEntity

object ApiClient : ApiRoutes {
    override suspend fun getExercises(): List<ExerciseEntity> {
        return listOf(
            ExerciseEntity(
                1,
                "Sentadillas",
                "3x15 repeticiones",
                "https://tse1.mm.bing.net/th/id/OIP.uSNK5ejNHJuvUNJTZTlFxAHaE8?rs=1&pid=ImgDetMain"
            ),
            ExerciseEntity(
                2,
                "Flexiones",
                "4x12 repeticiones",
                "https://th.bing.com/th/id/R.d859a5a197eec32024873c14eb099a7e?rik=%2bfz8ydP3serhLQ&pid=ImgRaw&r=0"
            ),
            ExerciseEntity(
                3,
                "Planchas",
                "3x60 segundos",
                "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/mid-adult-man-doing-plank-exercise-royalty-free-image-1585917009.jpg?resize=980:*"
            ),
            ExerciseEntity(
                4,
                "Press de banca",
                "4x10 repeticiones",
                "https://i0.wp.com/www.entrenamiento.com/wp-content/uploads/2018/11/mecanica-press-banca-720x466.jpg?resize=720%2C466&ssl=1"
            ),
            ExerciseEntity(
                5,
                "Remo con mancuernas",
                "3x12 repeticiones",
                "https://i.blogs.es/0c2131/remo-con-mancuerna/1366_2000.jpg"
            ),
            ExerciseEntity(
                6,
                "Peso muerto",
                "4x8 repeticiones",
                "https://th.bing.com/th/id/R.68861ecb440382aff77afd38b8d15dd9?rik=ghI5SNJJE%2brIAg&pid=ImgRaw&r=0"
            ),
            ExerciseEntity(
                7,
                "Zancadas",
                "3x12 cada pierna",
                "https://tse1.mm.bing.net/th/id/OIP.xbYcSVNrKhoRwFClup1W8wHaEo?rs=1&pid=ImgDetMain"
            ),
            ExerciseEntity(
                8,
                "Crunch abdominal",
                "4x20 repeticiones",
                "https://www.blog.kiffemybody.com/wp-content/uploads/2018/08/Crunch-abdominal-kiffemybody.jpg"
            ),
            ExerciseEntity(
                9,
                "Elevación de piernas",
                "3x15 repeticiones",
                "https://th.bing.com/th/id/R.f089a3607a27f827b5a2a9ed49349b25?rik=T1%2beWLLLYcYnZw&pid=ImgRaw&r=0"
            ),
            ExerciseEntity(
                10,
                "Oblicuos",
                "3x20 repeticiones",
                "https://tse2.mm.bing.net/th/id/OIP.nVRIybSfE2_2NJhlcz4HJwHaE8?rs=1&pid=ImgDetMain"
            )
        )
    }


    override suspend fun getRoutines(): List<RoutineEntity> {
        return listOf(
            RoutineEntity(
                1, "Full Body Blast", "Rutina completa de cuerpo",
                "https://www.nutrides.com/wp-content/uploads/2023/04/rutinas-full-body-para-entrenar-tu-cuerpo-entero-actualizadas.jpg",
                exerciseIds = "1,2,3"
            ),
            RoutineEntity(
                2, "Upper Body Strength", "Pecho, espalda y hombros",
                "https://www.cortaporlosano.com/pics/2025/01/work-every-muscle-with-this-complete-full-body-dumbbell-workout-no-gym-needed.jpg",
                exerciseIds = "4,5"
            ),
            RoutineEntity(
                3, "Lower Body Power", "Piernas y glúteos",
                "https://tse3.mm.bing.net/th/id/OIP.rn7uUSyMHDgkHepEGND-aQHaFA?rs=1&pid=ImgDetMain",
                exerciseIds = "6,7"
            ),
            RoutineEntity(
                4, "Core Crusher", "Abdominales intensivos",
                "https://th.bing.com/th/id/OIP.wDRCPU8Cuxw9tKZT4mtVvQHaE8?w=303&h=202&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                exerciseIds = "8,9,10"
            )
        )
    }

    override suspend fun getPosts(): List<NoteEntity> {
        return listOf(
            NoteEntity(
                id = 1,
                header = "Lucas Trainer",
                postText = "¡Entrenamiento completado! 💪 Hoy superé mis marcas en press de banca.",
                timestamp = System.currentTimeMillis()
            ),
            NoteEntity(
                id = 2,
                header = "Ana FitGirl",
                postText = "Amo mis rutinas de pierna 🔥 ¿Quién más entrena duro los lunes?",
                timestamp = System.currentTimeMillis() - 3600000
            ),
            NoteEntity(
                id = 3,
                header = "Carlos Pro",
                postText = "Sin excusas. 5 AM y en el gym 💯.",
                timestamp = System.currentTimeMillis() - 7200000
            )
        )
    }

    override suspend fun getUser(): UserEntity {
        // Devuelve un UserEntity de ejemplo
        return UserEntity(
            id = 1,
            name = "Lucas Trainer",
            email = "lucas@example.com",
            weight = 75.5f,
            height = 1.80f,
            age = 28,
            goal = "Ganar masa muscular",
            streakDays = 5,
            profileImage = "https://i.pravatar.cc/300?img=3"
        )
    }
}
