package com.example.frontsharestorage.DTO

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager private constructor(){
    private val retrofit = Retrofit.Builder()
        //.baseUrl("http://34.22.64.197:8080")
        //.baseUrl("https://api.mungnyang.site")
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService :ApiService = retrofit.create(ApiService::class.java)

    companion object{
        val instance by lazy { RetrofitManager() }
    }

}