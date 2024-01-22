package com.example.frontsharestorage.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import com.naver.maps.map.overlay.OverlayImage
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val markersWithInfo: MutableList<MarkerWithInfo> = mutableListOf()

    // BottomSheet layout 변수
    private val bottomSheetLayout by lazy { binding.bottomSheetLayout }

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
            hideKeyboard()
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

        initializePersistentBottomSheet()
        persistentBottomSheetEvent()

        return view
    }

    // Persistent BottomSheet 초기화
    // Persistent BottomSheet 초기화
    private fun initializePersistentBottomSheet() {

        // BottomSheetBehavior에 layout 설정
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        // CoordinatorLayout을 찾아서 CoordinatorLayout에 뷰를 추가
        val coordinatorLayout = view?.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        coordinatorLayout?.addView(bottomSheetLayout)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                // BottomSheetBehavior state에 따른 이벤트
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d("MainActivity", "state: hidden")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("MainActivity", "state: expanded")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("MainActivity", "state: collapsed")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d("MainActivity", "state: dragging")
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        Log.d("MainActivity", "state: settling")
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        Log.d("MainActivity", "state: half expanded")
                    }
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })

    }

    private fun searchVolunteer(keyword: String) {
        disableUserInput()
        showProgressBar()
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
                    "30"
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

                                GlobalScope.launch(Dispatchers.Main) {
                                    addMarker(
                                        coordinates.first,
                                        coordinates.second,
                                        item.progrmSj ?: "",
                                        actPlace,
                                        item.url ?: "",
                                        formatDate(item.progrmBgnde ?: ""),
                                        formatTime(item.actBeginTm ?: ""),
                                        item.srvcClCode ?: ""
                                    )
                                }
                            } else {
                                Log.d("Coordinates", "Unable to get coordinates for Act Place: $actPlace")
                            }
                        } else {
                            Log.d("Coordinates", "Act Place is null or blank for some item.")
                        }

                        val formattedDate = formatDate(item.progrmBgnde ?: "N/A")
                        val formattedTime = formatTime(item.actBeginTm ?: "N/A")

                        Log.d("Program Title", item.progrmSj ?: "N/A")
                        Log.d("Act Place", actPlace ?: "N/A")
                        Log.d("url", item.url ?: "N/A")
                        Log.d("progrmBgnde", item.progrmBgnde ?: "N/A")
                        Log.d("actBeginTm", item.actBeginTm ?: "N/A")
                        Log.d("srvcClCode", item.srvcClCode ?: "N/A")
                        enableUserInput()
                        hideProgressBar()
                    }
                } else {
                    Log.d("Network Error", "Error: ${response.code()}")
                    enableUserInput()
                    hideProgressBar()
                }
            } catch (e: Exception) {
                Log.d("Network Exception", e.message ?: "Unknown exception")
                enableUserInput()
                hideProgressBar()
            }
        }
    }

    data class MarkerWithInfo(
        val title: String,
        val location: String,
        val url: String,
        val date: String,
        val time: String,
        val field: String
    ) {
        fun copy(): MarkerWithInfo {
            return MarkerWithInfo(title, location, url, date, time, field)
        }
    }

// ...

    private fun addMarker(
        latitude: Double,
        longitude: Double,
        title: String,
        location: String,
        url: String,
        date: String,
        time: String,
        field: String
    ) {
        val marker = Marker()
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        // 마커 아이콘을 설정
        val icon = OverlayImage.fromResource(R.drawable.markericon)
        marker.icon = icon

        // 마커에 추가 정보를 저장
        val markerWithInfo = MarkerWithInfo(title, location, url, date, time, field)
        Log.d("MarkerInfo", "Title: $title, Location: $location, URL: $url, Date: $date, Time: $time, Field: $field")
        marker.setTag(markerWithInfo)

        marker.setOnClickListener { clickedMarker ->
            // 클릭 이벤트 핸들러에서 추가 정보를 가져와 사용
            val clickedMarkerInfo = (clickedMarker.tag as? MarkerWithInfo)?.copy()
            clickedMarkerInfo?.let { info ->
                Log.d("ClickedMarkerInfo", "Title: ${info.title}, Location: ${info.location}, URL: ${info.url}, Date: ${info.date}, Time: ${info.time}, Field: ${info.field}")
                showVolunteerInfo(info)
                fullsizeBottomSheetEvent()
                return@let true
            } ?: run {
                return@run false
            }
        }

        // 마커를 리스트에 추가
        markers.add(marker)

        // 중복 추가를 방지하기 위해 리스트에 없는 경우에만 추가
        if (!markersWithInfo.contains(markerWithInfo)) {
            markersWithInfo.add(markerWithInfo)
        }
    }

    private fun showVolunteerInfo(info: MarkerWithInfo) {
        val titleTextView = binding.titleTextView
        val locationTextView = binding.locationTextView
        val urlTextView = binding.urlTextView
        val dateTextView = binding.dateTextView
        val timeTextView = binding.timeTextView
        val fieldTextView = binding.fieldTextView

        // MarkerWithInfo 객체에서 정보 추출
        val title = info.title
        val location = info.location
        val url = info.url
        val date = info.date
        val time = info.time
        val field = info.field

        // TextView에 정보 설정
        titleTextView.text = "$title"
        locationTextView.text = "$location"
        urlTextView.text = "$url"
        dateTextView.text = "$date"
        timeTextView.text = "$time"
        fieldTextView.text = "$field"
    }





    private fun removeAllMarkers() {
        // 리스트에 있는 모든 마커 제거
        markers.forEach { it.map = null }
        // 리스트 비우기
        markers.clear()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
    private fun fullsizeBottomSheetEvent() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // PersistentBottomSheet 내부 버튼 click event
    // PersistentBottomSheet 내부 버튼 click event
    private fun persistentBottomSheetEvent() {
//
//        bottomSheetHidePersistentButton.setOnClickListener {
//            // BottomSheet 숨김
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        }
//
//        bottomSheetShowModalButton.setOnClickListener {
//            // 추후 modal bottomSheet 띄울 버튼
//        }

    }
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return inputDate // 변환이 실패하면 원본 날짜 반환
    }

    private fun formatTime(inputTime: String): String {
        try {
            val inputFormat = SimpleDateFormat("H", Locale.getDefault())
            val outputFormat = SimpleDateFormat("H시", Locale.getDefault())
            val time = inputFormat.parse(inputTime)
            return outputFormat.format(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return inputTime // 변환이 실패하면 원본 시간 반환
    }

    private fun disableUserInput() {
        // 사용자 입력 비활성화 로직 추가 (예: 버튼 클릭 이벤트 비활성화, 터치 이벤트 무시)
        binding.SearchingVolunteerButton.isEnabled = false
        // 추가로 사용자 입력을 비활성화해야 하는 경우 여기에 로직 추가
    }
    private fun showProgressBar() {
        binding.loadingLayout.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.loadingLayout.visibility = View.GONE
    }

    private fun enableUserInput() {
        // 사용자 입력 활성화 로직 추가
        binding.SearchingVolunteerButton.isEnabled = true
        // 추가로 사용자 입력을 활성화해야 하는 경우 여기에 로직 추가
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