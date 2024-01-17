package com.example.frontsharestorage.DTO

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException

class GeocodingHelper(private val context: Context) {

    fun getCoordinatesFromPlaceName(placeName: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context)

        try {
            val addresses: List<Address> = geocoder.getFromLocationName(placeName, 1)!!

            if (addresses.isNotEmpty()) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                return Pair(latitude, longitude)
            } else {
                Log.d("GeocodingHelper", "No coordinates found for place: $placeName")
            }

        } catch (e: IOException) {
            Log.e("GeocodingHelper", "Error converting place name to coordinates", e)
        }

        return null
    }
}