package com.example.frontsharestorage.Account

data class ResponseRankingDTO (
    var response: Boolean,
    val rankingList: List<UserRankingDTO>
)