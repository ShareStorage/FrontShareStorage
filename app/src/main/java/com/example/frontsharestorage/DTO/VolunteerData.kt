package com.example.frontsharestorage.DTO

data class VolunteerData(
    val accountID : Int,
    val recordID: Int,
    var title: String,
    var location: String,
    var detail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    val approve: Boolean?
)