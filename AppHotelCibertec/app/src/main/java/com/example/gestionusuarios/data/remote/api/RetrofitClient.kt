package com.example.gestionusuarios.data.remote

import android.content.Context
import com.example.gestionusuarios.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://172.20.10.3:8081/"
    //sesion con emulador
    //private const val BASE_URL = "http://10.0.2.2:8081/"

    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        // 1. Instanciamos el SessionManager
        val sessionManager = SessionManager(context)

        // 2. Usamos tu clase AuthInterceptor dedicada para manejar headers y el error 401
        val authInterceptor = AuthInterceptor(sessionManager)

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            // Añadimos el interceptor centralizado
            .addInterceptor(authInterceptor)
            // Añadimos el logger para depuración
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit?.create(serviceClass)
            ?: throw IllegalStateException("Retrofit no ha sido inicializado. Llama a RetrofitClient.init(context) en tu MainActivity.")
    }
}