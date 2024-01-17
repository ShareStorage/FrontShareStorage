package com.example.frontsharestorage.DTO

import retrofit2.Response
import retrofit2.http.GET
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
}

