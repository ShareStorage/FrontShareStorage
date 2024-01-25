package com.example.frontsharestorage.Account

import com.google.gson.annotations.SerializedName

data class AccountDTO (

    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("email")
    val email : String?,
    @SerializedName("nickName")
    val nickName: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("password")
    var password: String?

)