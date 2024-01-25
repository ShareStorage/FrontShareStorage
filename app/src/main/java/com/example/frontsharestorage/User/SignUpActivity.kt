package com.example.frontsharestorage.User

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontsharestorage.Account.AccountDTO
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var isEmailValid = false
    private var isDuplicateEmail = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitManager.instance


        // 이메일 입력 값의 유효성 검사
        binding.signupEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
                enableNextStepButton()
            }
        })

        binding.signupBackImage.setOnClickListener {
            finish()
        }

        binding.signupCheckButton.setOnClickListener {

            val emailEditText = binding.signupEmailEditText.text.toString()

            if(emailEditText.isEmpty()){
                Toast.makeText(this@SignUpActivity, "이메일을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                val sendValidCheck = retrofit.apiService.emailValidCheck(emailEditText);

                sendValidCheck.enqueue(object : Callback<ResponseDTO> {
                    override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                        val responseDto = response.body();

                        if (responseDto != null) {
                            if (responseDto.response) {
                                Toast.makeText(this@SignUpActivity, "사용 가능한 이메일 입니다!", Toast.LENGTH_SHORT).show()
                                isEmailValid = true
                            } else {
                                Toast.makeText(this@SignUpActivity, "사용 불가능한 이메일 입니다!", Toast.LENGTH_SHORT).show()
                                isEmailValid = false
                            }
                        } else {
                            Toast.makeText(this@SignUpActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                            isEmailValid = false
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("API TEST", "ERROR  = ${t.message}")
                    }
                })

            }
        }

        binding.signupSaveBarView.setOnClickListener {
            val userName = binding.nameEditText.text.toString()
            val email = binding.signupEmailEditText.text.toString()
            val userNickName = binding.signupNickNameEditText.text.toString()
            val userPhone = binding.signupPhoneEditText.text.toString()
            val password = binding.signupPasswordEditText.text.toString()

            // 필수 항목 체크
            if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || userNickName.isEmpty() || userPhone.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "필수 항목을 모두 작성 해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 중복 체크 여부 확인
            if (!isEmailValid) {
                Toast.makeText(this@SignUpActivity, "중복 체크를 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerDTO = AccountDTO("testestestestes", userName, email, userNickName, userPhone, password)

            val sendRegister = retrofit.apiService.register(registerDTO)
            sendRegister.enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        if (responseDto.response) {
                            Toast.makeText(this@SignUpActivity, "회원가입 되었습니다!", Toast.LENGTH_SHORT).show();
                            finish()
                        } else {
                            Toast.makeText(this@SignUpActivity, "회원가입 불가능!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(this@SignUpActivity, "회원가입불가능 네트워크 에러!", Toast.LENGTH_SHORT).show();
                }
            })
        }
    }

    private fun enableNextStepButton() {
        val nextStepButton = binding.signupSaveButtonFrame
        nextStepButton.isEnabled = isEmailValid && isDuplicateEmail
    }
}