package com.example.frontsharestorage.Fragment

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frontsharestorage.DTO.ApiService
import com.example.frontsharestorage.DTO.GeocodingHelper
import com.example.frontsharestorage.R
import com.example.frontsharestorage.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var geocodingHelper: GeocodingHelper
    private lateinit var apiService: ApiService
    private lateinit var binding: FragmentHomeBinding

    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView

    private val markers: MutableList<Marker> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        geocodingHelper = GeocodingHelper(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.SearchingVolunteerButton.setOnClickListener {
            val keyword = binding.SearchingVolunteerEditText.text.toString()
            searchVolunteer(keyword)
        }

        // recycleButton 클릭 이벤트 처리
        binding.recycleButton.setOnClickListener {
            // 모든 마커 제거
            removeAllMarkers()
            Log.d("HomeFragment.kt","마커 제거")
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://openapi.1365.go.kr/openapi/service/rest/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        return view
    }

    private fun searchVolunteer(keyword: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val today = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val todayString = dateFormat.format(today.time)

            val endDay = Calendar.getInstance()
            endDay.add(Calendar.DAY_OF_MONTH, 30)
            val endDayString = dateFormat.format(endDay.time)

            try {
                val response = apiService.getVolunteerList(
                    "prormSj",
                    keyword,
                    "3000000",
                    todayString,
                    endDayString,
                    "7"
                )

                if (response.isSuccessful) {
                    val openApiResponse = response.body()
                    val itemList = openApiResponse?.body?.items?.itemList

                    itemList?.forEach { item ->
                        val actPlace = item.actPlace
                        if (!actPlace.isNullOrBlank()) {
                            val coordinates = geocodingHelper.getCoordinatesFromPlaceName(actPlace)
                            if (coordinates != null) {
                                Log.d(
                                    "Coordinates for Act Place",
                                    "Act Place: $actPlace, Latitude: ${coordinates.first}, Longitude: ${coordinates.second}"
                                )
                                addMarker(coordinates.first, coordinates.second, actPlace)
                            } else {
                                Log.d("Coordinates", "Unable to get coordinates for Act Place: $actPlace")
                            }
                        } else {
                            Log.d("Coordinates", "Act Place is null or blank for some item.")
                        }

                        Log.d("Program Title", item.progrmSj ?: "N/A")
                        Log.d("Act Place", actPlace ?: "N/A")
                    }
                } else {
                    Log.d("Network Error", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("Network Exception", e.message ?: "Unknown exception")
            }
        }
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        val marker = Marker()
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap
        marker.icon = MarkerIcons.BLACK
        marker.captionText = title

        marker.setOnClickListener {
            showBottomSheet()
        }
        // 마커를 리스트에 추가
        markers.add(marker)
    }

    private fun showBottomSheet(): Boolean {
        val bottomSheetFragment = MarkerDetailsFragment()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        return true
    }

    private fun removeAllMarkers() {
        // 리스트에 있는 모든 마커 제거
        markers.forEach { it.map = null }
        // 리스트 비우기
        markers.clear()
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}