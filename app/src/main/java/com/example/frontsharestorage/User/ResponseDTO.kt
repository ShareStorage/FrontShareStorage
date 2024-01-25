package com.example.frontsharestorage.User

import com.google.gson.annotations.SerializedName

data class ResponseDTO (
    @SerializedName("response")
    var response : Boolean
)