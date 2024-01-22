package com.example.frontsharestorage.DTO

import com.naver.maps.map.overlay.Marker

data class MarkerWithInfo(
    var title: String,
    var location: String,
    var url: String,
    var date: String,
    var time: String,
    var field: String,
    val marker: Marker
)