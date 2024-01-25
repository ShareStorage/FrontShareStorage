package com.example.frontsharestorage.Fragment.Record

import com.google.gson.annotations.SerializedName

data class ResponseRecordDTO (
    @SerializedName("response")
    var response : Boolean,

    @SerializedName("recordList")
    val recordList: List<SearchResponseRecordDTO>
)