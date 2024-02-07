package com.example.frontsharestorage.Account

data class UserRankingDTO (
    val accountID : Int?,
    val userEmail : String?,
    var recordCount : Int?,
    var badge : String?,
    val userNickName : String?,
    val userImageURL : String?
)