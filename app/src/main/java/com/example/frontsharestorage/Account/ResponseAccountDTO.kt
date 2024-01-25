package com.example.frontsharestorage.Account

import com.google.gson.annotations.SerializedName

data class ResponseAccountDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("userList")
    val userList: List<SearchResponseAccountDTO>
)