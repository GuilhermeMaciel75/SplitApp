package melo.maciel.splitapp.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object UsingRetrofitInstance{
    private val BASE_URL = "htts://jsonplaceholder.typicode.com/"

    val loggin = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
        .addInterceptor(loggin)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object apiClient {
    val apiService: Api_Interface by lazy {
        UsingRetrofitInstance.retrofit.create(Api_Interface::class.java)
    }
}
