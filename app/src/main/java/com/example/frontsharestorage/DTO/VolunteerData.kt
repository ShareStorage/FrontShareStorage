package com.example.frontsharestorage.DTO

data class VolunteerData(
    val recordID: Int,
    val title: String,
    val location: String,
    val detail: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val approve: Boolean?
)