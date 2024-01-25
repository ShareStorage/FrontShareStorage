package com.example.frontsharestorage.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
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
import androidx.appcompat.app.AppCompatActivity
import com.example.frontsharestorage.Account.ResponseAccountDTO
import com.example.frontsharestorage.Activity.MainActivity
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.Fragment.Record.RecordDTO
import com.example.frontsharestorage.Fragment.Record.ResponseRecordDTO
import com.example.frontsharestorage.R
import com.example.frontsharestorage.User.LoginResponseDTO
import com.example.frontsharestorage.User.ResponseDTO
import com.example.frontsharestorage.databinding.FragmentHomeBinding
import com.example.frontsharestorage.databinding.FragmentRankingBinding
import com.example.frontsharestorage.databinding.FragmentUserBinding
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
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

    private var userID : Int = 0
    private var userEmail : String = ""
    private var userName : String = ""

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
        // searchRecordData(userID) 프래그먼트 넘어갈 때마다 기록 새로고침

        Log.d("userFragment에서 userID",userID.toString() )
        Log.d("userFragment에서 userEmail",userEmail)
        Log.d("userFragment에서 userName",userName)

        binding.addButton.setOnClickListener {
            showAlertDialog()
        }

        return view
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
//            val recordTitle = volunteerTitle.text.toString()
//            val recordLocation = volunteerLocation.text.toString()
//            val recordDetail = volunteerDetail.text.toString()
//            val recordDate = volunteerDateTextViewAlert.text.toString()
//            val recordTime = volunteerStartTimeTextViewAlert.text.toString() + "~" + volunteerEndTimeTextViewAlert.text.toString()
//
//            val recordDTO = RecordDTO(userID, recordTitle, recordLocation,recordDetail, recordDate, recordTime, null)
//            val sendRecord = retrofit.apiService.addRecord(recordDTO)
//            sendRecord.enqueue(object : Callback<ResponseDTO>{
//                override fun onResponse(call: retrofit2.Call<ResponseDTO>, response: Response<ResponseDTO>) {
//                    val responseDto = response.body()
//
//                    if (responseDto != null) {
//                        if (responseDto.response) {
//                            Toast.makeText(context, "기록 완료", Toast.LENGTH_SHORT).show()
//                            alertDialog?.dismiss()
//                            searchRecordData(userID)
//
//                        } else {
//                            Toast.makeText(context, "기록 불가능!", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Toast.makeText(context, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: retrofit2.Call<ResponseDTO>, t: Throwable) {
//                    Log.e("API TEST", "ERROR  = ${t.message}")
//                    Toast.makeText(context, "기록 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
//                }
//            })
            
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
//    private fun searchRecordData(accountID : Int) {
//        val retrofit = RetrofitManager.instance
//
//        val sendRecordSearch = retrofit.apiService.searchRecordData(accountID)
//        sendRecordSearch.enqueue(object : Callback<ResponseRecordDTO> {
//            override fun onResponse(
//                call: retrofit2.Call<ResponseRecordDTO>,
//                response: Response<ResponseRecordDTO>
//            ) {
//                val responseDto = response.body()
//                Log.d("dxawdwadawdawd", responseDto.toString())
//                if (responseDto != null) {
//                    val recordList = responseDto.recordList
//
//                    if (recordList.isNotEmpty()) {
//                        for (record in recordList) {
//                            val searchRecordTitle = record.recordTitle.toString()
//                            val searchRecordLocation = record.recordLocation.toString()
//                            val searchRecordDetail = record.recordDetail.toString()
//                            val searchRecordDay = record.recordDay.toString()
//                            val searchRecordTime = record.recordTime.toString()
//                            val searchRecordApprove = record.recordApprove.toString()
//
//                            binding.textView.text = searchRecordTitle
//                            binding.textView2.text = searchRecordLocation
//                            binding.textView3.text = searchRecordDetail
//                            binding.textView4.text = searchRecordDay
//                            binding.textView5.text = searchRecordTime
//                            binding.textView6.text = searchRecordApprove
//
//                            Log.d("userFragment에서 searchRecordTitle",searchRecordTitle)
//                            Log.d("userFragment에서 searchRecordLocation",searchRecordLocation)
//                            Log.d("userFragment에서 searchRecordDetail",searchRecordDetail)
//                            Log.d("userFragment에서 searchRecordDay",searchRecordDay)
//                            Log.d("userFragment에서 searchRecordTime",searchRecordTime)
//                            Log.d("userFragment에서 searchRecordApprove",searchRecordApprove)
//
//                        }
//                    } else {
//                        Toast.makeText(context, "기록이 없습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(context, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<ResponseRecordDTO>, t: Throwable) {
//                Toast.makeText(context, "검색 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

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