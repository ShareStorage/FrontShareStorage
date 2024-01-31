package com.example.frontsharestorage.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.DTO.VolunteerAdapter
import com.example.frontsharestorage.DTO.VolunteerData
import com.example.frontsharestorage.Fragment.Record.RecordDTO
import com.example.frontsharestorage.Fragment.Record.ResponseCountDTO
import com.example.frontsharestorage.Fragment.Record.ResponseRecordDTO
import com.example.frontsharestorage.Fragment.Record.recordCountOBJ
import com.example.frontsharestorage.R
import com.example.frontsharestorage.User.ResponseDTO
import com.example.frontsharestorage.databinding.FragmentUserBinding

import retrofit2.Call

import com.google.firebase.storage.FirebaseStorage
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UserFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUserBinding
    private lateinit var volunteerDateTextView: TextView
    private lateinit var volunteerDateSelectButton: ImageView
    private var alertDialog: AlertDialog? = null
    private val retrofit = RetrofitManager.instance

    private lateinit var recyclerView: RecyclerView
    private lateinit var volunteerAdapter: VolunteerAdapter
    private var volunteerList : MutableList<VolunteerData> = mutableListOf()


    private var userID : Int = 0
    private var userEmail : String = ""
    private var userName : String = ""


    private val PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 201
    private var currentPhotoPath: String? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var URL = ""
    private var selectedImageUri: Uri? = null  // 이 부분을 추가
    private val GALLERY_REQUEST_CODE = 202
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container,false)
        val view = binding.root
        // fragment_user.xml 레이아웃에서 위젯 참조

        val args = arguments
        userID = args?.getInt("userID", 0)!!
        userEmail = args?.getString("userEmail")!!
        userName = args?.getString("userName")!!
        searchRecordData(userID)

        recordCountOBJ.recordCount(userID, binding, requireContext())

        Log.d("userFragment에서 userID",userID.toString() )
        Log.d("userFragment에서 userEmail",userEmail)
        Log.d("userFragment에서 userName",userName)

//        recyclerView = view.findViewById(R.id.recyclerView)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(context)

        initializeViews()

        binding.addButton.setOnClickListener {
            showAlertDialog()
        }

        return view
    }
    private fun initializeViews() {
        // 아이템들을 어떻게 배열할지 정해줌
        binding.recordRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        volunteerAdapter = VolunteerAdapter(context, volunteerList)
        binding.recordRecyclerView.adapter = volunteerAdapter
    }

    @SuppressLint("MissingInflatedId")
    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.add_volunteer_alertdialog, null)

        // add_volunteer_alertdialog.xml에서 위젯 참조
        val volunteerDateTextViewAlert = dialogView.findViewById<TextView>(R.id.volunteerDateTextView)
        val volunteerDateSelectButtonAlert = dialogView.findViewById<ImageView>(R.id.volunteerdateselectbutton)
        val volunteerStartTimeTextViewAlert = dialogView.findViewById<TextView>(R.id.volunteerStartTime)
        val volunteerEndTimeTextViewAlert = dialogView.findViewById<TextView>(R.id.volunteerEndTime)
        val volunteerStartTimeButtonAlert = dialogView.findViewById<ImageView>(R.id.volunteerstarttimebutton)
        val volunteerEndTimeButtonAlert = dialogView.findViewById<ImageView>(R.id.volunteerendtimebutton)
        val volunteerSaveButton = dialogView.findViewById<View>(R.id.volunteerSaveButton)
        val volunteerTitle = dialogView.findViewById<EditText>(R.id.volunteerTitleEditText)
        val volunteerLocation = dialogView.findViewById<EditText>(R.id.volunteerLocationEditText)
        val volunteerDetail = dialogView.findViewById<EditText>(R.id.volunteerDetailEditText)

        val volunteerCameraButtonAlert = dialogView.findViewById<ImageView>(R.id.cameraButton)
        val volunteerGalleryButtonAlert = dialogView.findViewById<ImageView>(R.id.galleryButton)
        val volunteerImageInfoTextViewAlert = dialogView.findViewById<TextView>(R.id.imageinfoTextView)

        val imageinfoTextView = dialogView.findViewById<TextView>(R.id.imageinfoTextView)


        volunteerCameraButtonAlert.setOnClickListener {

            // 카메라 버튼 클릭 시 이미지 촬영 가능하게
            checkCameraPermission()
            volunteerImageInfoTextViewAlert.text = "업로드 완료"
        }

        volunteerGalleryButtonAlert.setOnClickListener {
            // 갤러리 버튼 클릭 시 갤러리에서 이미지 가져오는거 가능하게

            openGallery()
            volunteerImageInfoTextViewAlert.text = "업로드 완료"
        }

        volunteerDateSelectButtonAlert.setOnClickListener {
            showDatePickerDialogForAlertDialog(volunteerDateTextViewAlert)
        }

        volunteerStartTimeButtonAlert.setOnClickListener {
            showTimePickerDialogForAlertDialog(volunteerStartTimeTextViewAlert)
        }

        volunteerEndTimeButtonAlert.setOnClickListener {
            showTimePickerDialogForAlertDialog(volunteerEndTimeTextViewAlert)
        }

        volunteerSaveButton.setOnClickListener{

            // 기록하기 버튼 클릭 시 데이터 서버에 저장
            val recordTitle = volunteerTitle.text.toString()
            val recordLocation = volunteerLocation.text.toString()
            val recordDetail = volunteerDetail.text.toString()
            val recordDate = volunteerDateTextViewAlert.text.toString()
            val recordStartTime = volunteerStartTimeTextViewAlert.text.toString()
            val recordEndTime = volunteerEndTimeTextViewAlert.text.toString()
            val recordImageURL = volunteerImageInfoTextViewAlert.text.toString()

            val recordDTO = RecordDTO(userID, recordTitle, recordLocation, recordDetail, recordDate, recordStartTime, recordEndTime, recordImageURL, false)
            val sendRecord = retrofit.apiService.addRecord(recordDTO)
            sendRecord.enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: retrofit2.Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    val responseDto = response.body()

                    if (responseDto != null) {
                        if (responseDto.response) {
                            Toast.makeText(context, "기록 완료", Toast.LENGTH_SHORT).show()
                            alertDialog?.dismiss()
                            searchRecordData(userID)
                            recordCountOBJ.recordCount(userID, binding, requireContext())

                        } else {
                            Toast.makeText(context, "기록 불가능!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(context, "기록 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
                }
            })

            // 이미지를 Firebase Storage에 업로드
            if (currentPhotoPath != null) {
                uploadImageToFirebaseStorage(currentPhotoPath!!)
            }

        }

        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.show()

    }

    private fun showDatePickerDialogForAlertDialog(volunteerDateTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // 사용자가 날짜를 선택했을 때 호출되는 콜백
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                // 선택한 날짜를 원하는 형식으로 변환
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                // TextView에 선택한 날짜 설정
                volunteerDateTextView.text = formattedDate
            },
            year,
            month,
            day
        )

        // DatePickerDialog 표시
        datePickerDialog.show()
    }

    // DB에서 사용자 식별키로 기록한 기록들 가져옴
    private fun searchRecordData(accountID : Int) {
        volunteerList.clear()
        val retrofit = RetrofitManager.instance

        val sendRecordSearch = retrofit.apiService.searchRecordData(accountID)
        sendRecordSearch.enqueue(object : Callback<ResponseRecordDTO> {
            override fun onResponse(
                call: retrofit2.Call<ResponseRecordDTO>,
                response: Response<ResponseRecordDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val recordList = responseDto.recordList

                    if (recordList.isNotEmpty()) {
                        for (record in recordList) {
                            val searchAccountID = record.accountID
                            val searchRecordID = record.recordID
                            val searchRecordTitle = record.recordTitle.toString()
                            val searchRecordLocation = record.recordLocation.toString()
                            val searchRecordDetail = record.recordDetail.toString()
                            val searchRecordDay = record.recordDay.toString()
                            val searchRecordStartTime = record.recordStartTime.toString()
                            val searchRecordEndTime = record.recordEndTime.toString()
                            val searchRecordApprove = record.recordApprove

                            val searchData = VolunteerData(searchAccountID!!, searchRecordID!!, searchRecordTitle, searchRecordLocation, searchRecordDetail, searchRecordDay, searchRecordStartTime, searchRecordEndTime,
                                searchRecordApprove)
                            volunteerList.add(searchData)

                        }
                        volunteerAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "기록이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseRecordDTO>, t: Throwable) {
                Toast.makeText(context, "검색 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun showTimePickerDialogForAlertDialog(targetTextView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // 사용자가 시간을 선택했을 때 호출되는 콜백
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)

                // 선택한 시간을 원하는 형식으로 변환
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedTime.time)

                // TextView에 선택한 시간 설정
                targetTextView.text = formattedTime
            },
            hour,
            minute,
            true // 24시간 형식으로 표시
        )

        // TimePickerDialog 표시
        timePickerDialog.show()
    }

    private fun checkCameraPermission() {
        Log.d("UserFragment.kt","checkCameraPermission 함수 호출")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없을 경우 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 있을 경우 카메라 열기
            openCamera()
        }
    }

    private fun openCamera() {
        // 카메라 앱을 열어 사진 촬영
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.frontsharestorage.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지 파일 이름 생성
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // 파일 경로 저장
            currentPhotoPath = absolutePath
        }
    }


    private fun uploadImageToFirebaseStorage(filePath: String) {
        Log.d("UserFragment.kt","uploadImageToFirebaseStorage 함수 호출")
        val file = Uri.fromFile(File(filePath))
        val imagesRef = storageRef.child("images/${file.lastPathSegment}")

        imagesRef.putFile(file)
            .addOnSuccessListener { taskSnapshot ->
                // 이미지 업로드 성공 시
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    // 업로드된 이미지의 다운로드 URL 획득
                    URL = uri.toString()
                    // 여기에서 URL을 활용하여 필요한 작업 수행 가능
                    Log.d("Firebase Storage URL", URL)
                    // 이후에 필요한 작업 수행 가능
                    // 예: 서버에 URL 업로드, 이미지 정보 저장 등
                }
            }
            .addOnFailureListener { exception ->
                // 이미지 업로드 실패 시
                Log.e("Firebase Storage", "Image upload failed: ${exception.message}")
                Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    // onActivityResult 메서드 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 갤러리에서 이미지를 선택했을 때 처리할 내용
            selectedImageUri = data?.data
            // 토스트 메시지로 선택한 이미지의 Uri 확인
            Toast.makeText(requireContext(), "Selected Image: $selectedImageUri", Toast.LENGTH_SHORT).show()
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 카메라로 이미지를 촬영했을 때 처리할 내용
            // ...
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}