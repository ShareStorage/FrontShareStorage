package com.example.frontsharestorage.Account

import com.google.gson.annotations.SerializedName

data class SearchResponseAccountDTO (

    @SerializedName("accountID")
    val accountID: Int?,

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("userName")
    val userName: String?,

    @SerializedName("email")
    val email : String?,

    @SerializedName("nickName")
    val nickName: String?,

    @SerializedName("phone")
    val phone: String?
)