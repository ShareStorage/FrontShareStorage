package com.example.frontsharestorage.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.frontsharestorage.R
import com.example.frontsharestorage.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.frontsharestorage.Fragment.HomeFragment
import com.example.frontsharestorage.Fragment.RankingFragment
import com.example.frontsharestorage.Fragment.UserFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var bnv_main: BottomNavigationView // bnv_main을 선언

    private var userID : Int = 0
    private var userEmail : String = ""
    private var userName : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userID = intent.getIntExtra("userID", 0)
        userEmail = intent.getStringExtra("userEmail").toString()
        userName = intent.getStringExtra("userName").toString()

        Log.d("메인에서 userID", userID.toString())
        Log.d("메인에서 userEmail", userEmail)
        Log.d("메인에서 userName", userName)

        bnv_main = findViewById(R.id.bottom_navigationview)

        bnv_main.setOnItemSelectedListener { item ->
            val args = Bundle()

            args.putInt("userID", userID)
            args.putString("userEmail", userEmail)
            args.putString("userName", userName)

            changeFragment(
                when (item.itemId) {
                    R.id.homeNavigation -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_fragment
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_fragment
                        )

                        val fragment = HomeFragment()
                        val bundle = Bundle()

                        fragment

                    }
                    R.id.userNavigation -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_fragment
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_fragment
                        )

                        val fragment = UserFragment()

                        fragment.apply {
                            arguments = args
                        }

                    }

                    else -> {
                        bnv_main.itemIconTintList = ContextCompat.getColorStateList(this,
                            R.color.click_fragment
                        )
                        bnv_main.itemTextColor = ContextCompat.getColorStateList(this,
                            R.color.click_fragment
                        )

                        val fragment = RankingFragment()
                        fragment.apply {
                            arguments = args
                        }

                    }
                }
            )
            true
        }
        bnv_main.selectedItemId = R.id.homeNavigation
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
