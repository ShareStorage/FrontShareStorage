package com.example.frontsharestorage.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontsharestorage.Account.ResponseRankingDTO
import com.example.frontsharestorage.Account.UserRankingDTO
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.databinding.FragmentRankingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var userEmail : String = ""

class RankingFragment : Fragment() {

    private var _binding : FragmentRankingBinding? = null
    private val retrofit = RetrofitManager.instance
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var rankingAdapter: RankingAdapter
    private val allUserList: ArrayList<UserRankingDTO> = ArrayList()
    private lateinit var rankingItems: List<RankingItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        recyclerView = binding.rankingRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터 설정
        rankingAdapter = RankingAdapter()
        recyclerView.adapter = rankingAdapter
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        val args = arguments

        userEmail = args?.getString("userEmail")!!

        searchAllRankingData()
        Log.d("RankingFragment에서 이메일", userEmail)

        return binding.root
    }
    private fun searchAllRankingData() {

        val sendAllRankingSearch = retrofit.apiService.searchRankingAllData()
        sendAllRankingSearch.enqueue(object : Callback<ResponseRankingDTO> {
            override fun onResponse(
                call: Call<ResponseRankingDTO>,
                response: Response<ResponseRankingDTO>
            ) {
                if (response.isSuccessful) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        val sortedList = responseDto.rankingList.sortedWith(
                            compareByDescending<UserRankingDTO> { it.recordCount }
                                .thenBy { it.accountID }
                        )

                        rankingItems = sortedList.map { user ->
                            RankingItem(
                                nickName = user.userNickName,
                                recordCount = user.recordCount,
                                badge = user.badge,
                                imageURL = user.userImageURL
                            )
                        }

                        // 어댑터에 리스트 전달
                        rankingAdapter.submitList(rankingItems)
                        rankingAdapter.notifyDataSetChanged()


                        allUserList.clear()
                        allUserList.addAll(sortedList)

                        Log.d("sortedList", allUserList.toString())

                    } else {
                        Log.d(ContentValues.TAG, "Search Response is null")
                    }
                } else {
                    Log.e(ContentValues.TAG, "Search Request Failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ResponseRankingDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

