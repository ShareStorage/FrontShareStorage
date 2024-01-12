package com.example.frontsharestorage.Fragment

import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frontsharestorage.DTO.RetrofitClient
import com.example.frontsharestorage.R
import com.example.frontsharestorage.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

val programRegistNo = "your_program_regist_no"
val apiKey = "dj%2FPwMFfIrukwojmFhvpYJXnCVtZjSiNR74KVC8oUUSicaIR7YiKek1YdwAx4h10r1Sq80mDJSRlHFsVmYUGIw%3D%3D"


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.apiService.getVolunteerDetails(programRegistNo, apiKey)
                if (response.isSuccessful) {
                    val volunteerDetails = response.body()
                    // 성공 시 로그
                    Log.d("API_CALL", "API 호출 성공: $volunteerDetails")
                } else {
                    // 실패 시 로그
                    Log.e("API_CALL", "API 호출 실패: ${response.code()} - ${response.message()}")
                }
            }catch (e: Throwable) {
                // 네트워크 오류 시 로그
                Log.e("API_CALL", "네트워크 오류 발생: ${e.message}")
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
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