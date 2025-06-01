package com.example.fittrack.api

import com.example.fittrack.entity.*
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient : ApiRoutes {
    private const val BASE_URL = "https://f7d0-46-6-130-69.ngrok-free.app/"

    private val retrofitService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

//    override suspend fun getExercises(): List<ExerciseEntity> {
//        val response = retrofitService.getExercises()
//        return response.body().orEmpty()
//    }
override suspend fun getExercises(): List<ExerciseEntity> {
    val defaultImageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"

    return listOf(
        ExerciseEntity(
            id = 1,
            name = "Sentadillas",
            description = "Ejercicio fundamental para fortalecer piernas y glúteos. Mantén la espalda recta y baja hasta formar un ángulo de 90 grados en las rodillas.",
            imageUri = "https://th.bing.com/th/id/R.b9234ae5e8cd1c9baf50d253653945c4?rik=%2b%2fzp3l%2feSwfLtw&riu=http%3a%2f%2fdeportesok.com%2fwp-content%2fuploads%2f2017%2f06%2f2-3.jpg&ehk=PqcZrtSp7hWSDpMCFjfTiHVSfioHtr%2bChnOaOXfEFLw%3d&risl=&pid=ImgRaw&r=0"
        ),
        ExerciseEntity(
            id = 2,
            name = "Flexiones",
            description = "Trabaja el pecho, tríceps y hombros. Mantén el cuerpo recto al bajar y subir, sin arquear la espalda.",
            imageUri = "https://th.bing.com/th/id/R.d859a5a197eec32024873c14eb099a7e?rik=%2bfz8ydP3serhLQ&pid=ImgRaw&r=0"
        ),
        ExerciseEntity(
            id = 3,
            name = "Plancha",
            description = "Fortalece el core. Apóyate en antebrazos y pies, manteniendo el cuerpo alineado y sin bajar la cadera.",
            imageUri = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/mid-adult-man-doing-plank-exercise-royalty-free-image-1585917009.jpg?resize=980:*"
        ),
        ExerciseEntity(
            id = 4,
            name = "Abdominales",
            description = "Ejercicio clásico para trabajar el abdomen. Eleva el torso sin despegar la espalda baja del suelo.",
            imageUri = "https://th.bing.com/th/id/OIP.kCgiAuKNIKRN05F15MNlbgHaEM?rs=1&pid=ImgDetMain"
        ),
        ExerciseEntity(
            id = 5,
            name = "Zancadas",
            description = "Fortalece piernas y glúteos. Da un paso hacia adelante y baja hasta que ambas piernas formen ángulos rectos.",
            imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"
        ),
                ExerciseEntity(id = 6, name = "Press de banca con barra", description = "Trabaja el pecho, hombros y tríceps. Usa un agarre medio y baja la barra hasta el pecho.",
                    imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 7, name = "Press inclinado con mancuernas", description = "Aísla la parte superior del pectoral. Realiza el movimiento de forma controlada.",
        imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 8, name = "Remo con barra", description = "Ejercicio de espalda que mejora el grosor. Mantén la espalda recta y el core contraído.",
        imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 9, name = "Dominadas", description = "Ejercicio compuesto para espalda y bíceps. Sube hasta que la barbilla supere la barra."
        ,  imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 10, name = "Curl de bíceps con barra", description = "Trabaja los bíceps. Evita balancearte para maximizar el estímulo."
        ,  imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"
    ),
    ExerciseEntity(id = 11, name = "Press militar con mancuernas", description = "Enfocado en los hombros. Empuja las mancuernas por encima de la cabeza de forma controlada."
        ,  imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 12, name = "Extensiones de tríceps en polea", description = "Aísla el tríceps. Mantén los codos fijos y cerca del cuerpo."
        ,  imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 13, name = "Peso muerto", description = "Ejercicio compuesto que trabaja espalda baja, glúteos y piernas. Técnica estricta es clave."
        ,  imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
    ExerciseEntity(id = 14, name = "Prensa de piernas", description = "Fortalece cuádriceps y glúteos. Ajusta el respaldo para una postura segura."
        ,  imageUri = "https://cdn.static.aptavs.com/imagenes/zancadas-como-se-hacen-beneficios-y-musculos-trabajados_748x499.jpg"),
        ExerciseEntity(id = 15, name = "Elevaciones laterales", description = "Para deltoides medios. Levanta los brazos a la altura de los hombros sin impulso.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 16, name = "Press pecho horizontal con mancuernas", description = "Activa pectorales, tríceps y hombros. Mantén los brazos controlados y baja hasta el nivel del pecho.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 17, name = "Press superior en máquina", description = "Enfocado en el pectoral superior. Usa un agarre firme y empuja de forma constante.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 18, name = "Remo en polea baja", description = "Trabaja la espalda media. Tira del agarre hacia el abdomen manteniendo el torso estable.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 19, name = "Jalón al pecho en polea", description = "Ejercicio para dorsales. Baja la barra al pecho sin balancearte.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 20, name = "Curl alterno con mancuernas", description = "Aísla cada bíceps. Realiza el movimiento de forma lenta y sin impulso.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 21, name = "Press de hombros con barra", description = "Trabaja deltoides. Levanta la barra desde el pecho hasta extender los brazos totalmente.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 22, name = "Copa con mancuerna para tríceps", description = "Enfocado en la cabeza larga del tríceps. Mantén los codos estables.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 23, name = "Peso muerto rumano", description = "Trabaja femorales y glúteos. Baja la barra manteniendo la espalda recta y piernas semi extendidas.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 24, name = "Hack squat", description = "Variante de sentadilla en máquina. Ideal para cuádriceps con menor carga en espalda baja.",
            imageUri = defaultImageUri),
        ExerciseEntity(id = 25, name = "Vuelos laterales en polea", description = "Aísla los deltoides laterales. Levanta los brazos desde los costados manteniendo tensión constante.",
            imageUri = defaultImageUri)
    // Puedes continuar añadiendo los demás aquí hasta el id = 70
    )
}

    override suspend fun getTargetLocations(): List<TargetLocationEntity> {
       return listOf(
            TargetLocationEntity(
                id = 1,
                name = "Googleplex - Google HQ",
                position = LatLng(37.4220541, -122.0853242), // 1600 Amphitheatre Pkwy, Mountain View, CA
                radiusMeters = 200.0
            ))
    }
    override suspend fun getRoutines(userId: Int): List<RoutineEntity> {
        val response = retrofitService.getRoutines(userId)
        return response.body().orEmpty()
    }

    override suspend fun getUser(): UserEntity {
        return UserEntity(
            id = 1,
            name = "Lucas Trainer",
            email = "lucas@example.com",
            streakDays = 1,
            profileImage = "https://i.pravatar.cc/300?img=3"
        )
    }

    override suspend fun getNotes(userId: Int): List<NoteEntity> {
        val response = retrofitService.getNotes(userId)
        return response.body().orEmpty()
    }

    override suspend fun getExerciseLogs(userId: Int): List<ExerciseLogEntity> {
        val response = retrofitService.getExerciseLogs(userId)
        return response.body().orEmpty()
    }
}
