package com.example.frontsharestorage.Fragment.Record

import com.google.gson.annotations.SerializedName

data class SearchResponseRecordDTO (

    @SerializedName("recordID")
    val recordID: Int?,

    @SerializedName("accountID")
    val accountID: Int?,

    @SerializedName("recordTitle")
    val recordTitle: String?,

    @SerializedName("recordLocation")
    val recordLocation: String?,

    @SerializedName("recordDetail")
    val recordDetail : String?,

    @SerializedName("recordDay")
    val recordDay: String?,

    @SerializedName("recordTime")
    val recordTime: String?,

    @SerializedName("recordApprove")
    val recordApprove: Boolean?
)