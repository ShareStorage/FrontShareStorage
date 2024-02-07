package com.example.frontsharestorage.Fragment.Record

import android.content.Context
import android.widget.Toast
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.databinding.FragmentUserBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object recordCountOBJ {
    private val retrofit = RetrofitManager.instance
    fun recordCount(accountID: Int,
                    binding: FragmentUserBinding,
                    context: Context)
    {
        val sendRecordCount = retrofit.apiService.recordCount(accountID)
        sendRecordCount.enqueue(object : Callback<ResponseCountDTO> {
            override fun onResponse(
                call: Call<ResponseCountDTO>,
                response: Response<ResponseCountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {
                    if (responseDto.response) {

                        binding.allApproveCount.text = "총 봉사 횟수는 " + responseDto.approveCount.toString() + "회 입니다."
                        binding.approveCount.text = responseDto.approveCount.toString() + "회"
                        binding.disApproveCount.text = responseDto.disApproveCount.toString() + "회"
                        binding.recordCount.text = responseDto.recordCount.toString() + "회"

                    } else {
                        Toast.makeText(context, "기록 횟수 띄우기 불가능!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseCountDTO>, t: Throwable) {
                Toast.makeText(context, "검색 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}