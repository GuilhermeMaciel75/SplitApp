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

    // registrar um novo group
    @POST("/register/group")
    fun register(@Body userGroupData: GroupData): Call<API_DATA>

    @GET("/login/group")
    fun loginGroup(
        @Query("login") username: String,
        @Query("pwd") password: String,
        @Query("loginUser") loginUser: String
    ): Call<GroupLogin>

    @GET("/groups/all")
    fun getAllGroups(): Call<GroupResponse>
}