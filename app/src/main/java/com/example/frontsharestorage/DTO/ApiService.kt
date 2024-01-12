package com.example.frontsharestorage.DTO

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("openapi/service/rest/VolunteerPartcptnService/getVltrPartcptnItem")
    suspend fun getVolunteerDetails(
        @Query("progrmRegistNo") programRegistNo: String,
        // 여기에 다른 필요한 파라미터 추가
        @Query("apiKey") apiKey: String
    ): Response<DataModel>
}