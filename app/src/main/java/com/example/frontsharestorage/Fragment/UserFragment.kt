package com.example.frontsharestorage.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.frontsharestorage.R
import com.example.frontsharestorage.databinding.FragmentHomeBinding
import com.example.frontsharestorage.databinding.FragmentRankingBinding
import com.example.frontsharestorage.databinding.FragmentUserBinding
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
            // 이 버튼 누르면 저장
        }

        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.show()
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
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