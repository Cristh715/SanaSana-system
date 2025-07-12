package com.goforest.sanasanasystem.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original: Request = chain.request()
                    val builder = original.newBuilder()
                    // Obtener el token JWT guardado en SharedPreferences
                    val sharedPref = context.getSharedPreferences("sana_sana_prefs", Context.MODE_PRIVATE)
                    val token= sharedPref.getString("auth_token", null)
                    if (!token.isNullOrEmpty()) {
                        Log.d("ApiClient", "Enviando token: $token")
                        builder.header("Authorization", "Bearer $token")
                    } else {
                        Log.d("ApiClient", "No se encontró token en SharedPreferences")
                    }
                    chain.proceed(builder.build())
                })
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }
} 