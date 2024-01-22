package com.example.frontsharestorage.DTO

import com.example.frontsharestorage.Fragment.HomeFragment
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

    @GET("VolunteerPartcptnService/getVolSearchWord")
    suspend fun getVolunteerDetail(
        @Query("SchCateGu") schCateGu: String,
        @Query("progrmSj") progrmSj: HomeFragment.MarkerWithInfo
    ): Response<OpenApiResponse>
}