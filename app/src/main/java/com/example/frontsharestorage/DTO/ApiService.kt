package com.example.frontsharestorage.DTO

import com.example.frontsharestorage.Account.AccountDTO
import com.example.frontsharestorage.Account.ResponseAccountDTO
import com.example.frontsharestorage.Fragment.HomeFragment
import com.example.frontsharestorage.Fragment.Record.RecordDTO
import com.example.frontsharestorage.Fragment.Record.ResponseRecordDTO
import com.example.frontsharestorage.User.LoginDTO
import com.example.frontsharestorage.User.LoginResponseDTO
import com.example.frontsharestorage.User.ResponseDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("VolunteerPartcptnService/getVltrSearchWordList")
    suspend fun getVolunteerList(
        @Query("SchCateGu") schCateGu: String,
        @Query("keyword") keyword: String,
        @Query("schSign1") schSign1: String,
        @Query("schprogrmBgnde") schprogrmBgnde: String,
        @Query("progrmEndde") progrmEndde: String,
        @Query("numOfRows") numOfRows: String
    ): Response<OpenApiResponse>

    @GET("VolunteerPartcptnService/getVolSearchWord")
    suspend fun getVolunteerDetail(
        @Query("SchCateGu") schCateGu: String,
        @Query("progrmSj") progrmSj: HomeFragment.MarkerWithInfo
    ): Response<OpenApiResponse>

    @GET("account/emailValid")
    fun emailValidCheck(@Query("email") email:String): Call<ResponseDTO>

    @POST("account/register")
    fun register(@Body body: AccountDTO): Call<ResponseDTO>

    @GET("account/searchUser")
    fun findUser(@Query("email") email:String): Call<ResponseAccountDTO>

    @POST("Login")
    fun login(@Body body: LoginDTO) : Call<LoginResponseDTO>

    @POST("record/addRecord")
    fun addRecord(@Body recordDTO: RecordDTO): Call<ResponseDTO>

    @GET("record/searchRecordData")
    fun searchRecordData(@Query("accountID") accountID:Int): Call<ResponseRecordDTO>

    @DELETE("record/deleteRecord")
    fun deleteRecord(@Query("recordID") recordID:Int): Call<ResponseDTO>
}