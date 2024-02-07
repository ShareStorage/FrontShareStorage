package com.example.frontsharestorage.Account

data class ResponseAllDataDTO(
    var response: Boolean,
    val userList: List<UserRankingDTO>
)
