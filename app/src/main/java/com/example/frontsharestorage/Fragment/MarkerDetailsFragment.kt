package com.example.frontsharestorage.Fragment

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.frontsharestorage.databinding.FragmentMarkerDetailsBinding

class MarkerDetailsFragment : DialogFragment() {

    private lateinit var binding: FragmentMarkerDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarkerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기에서 BottomSheet에 필요한 뷰 및 데이터를 설정합니다.
        // binding.textView.text = "Your Data"
    }
}