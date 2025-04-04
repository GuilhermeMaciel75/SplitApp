package melo.maciel.splitapp.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api_Interface {
    // login com parâmetros de login e senha
    @GET("/login")
    fun login(
        @Query("login") username: String,
        @Query("pwd") password: String
    ): Call<API_DATA>

    // registrar um novo usuário com nome, login, senha e email
    @POST("/register")
    fun register(@Body userData: UserData): Call<API_DATA>
}