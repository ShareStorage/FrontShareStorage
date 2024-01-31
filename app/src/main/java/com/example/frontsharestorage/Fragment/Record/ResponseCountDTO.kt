package com.example.frontsharestorage.Fragment.Record

data class ResponseCountDTO (
    val response: Boolean,
    val recordCount : Int,
    val approveCount : Int,
    val disApproveCount : Int
)