package com.example.frontsharestorage.User

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontsharestorage.Account.AccountDTO
import com.example.frontsharestorage.Account.ResponseAccountDTO
import com.example.frontsharestorage.Activity.MainActivity
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val SHARED_PREFS_NAME = "MyPrefs"
    private val TOKEN_KEY = "token"
    private val RC_SIGN_IN = 0
    private val retrofit = RetrofitManager.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitManager.instance


        binding.emailEditText.setText("test1234@daum.net")
        binding.passwordEditText.setText("1q2w3e4r!")

        // 로그인 버튼
        binding.loginBtn.setOnClickListener{
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val loginDTO = LoginDTO(email, password)

            val sendLogin = retrofit.apiService.login(loginDTO)
            sendLogin.enqueue(object : Callback<LoginResponseDTO> {

                override fun onResponse(call: Call<LoginResponseDTO>, response: Response<LoginResponseDTO>) {
                    val responseLoginDto = response.body()

                    if (responseLoginDto != null) {
                        Log.d("responseLoginDto", responseLoginDto.toString())
                        Log.d("response", responseLoginDto.success.toString())
                        // 토큰 파싱 및 로그인 성공 처리
                        if (responseLoginDto.success) {
                            // 토큰 파싱
                            val token = response.headers()["Authorization"]?.replace("Bearer ", "")
                            if (token != null) {
                                // 토큰을 SharedPreferences에 저장
                                val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                                sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

                                // 토큰을 SharedPreferences에서 가져오기
                                val savedToken = sharedPreferences.getString(TOKEN_KEY, null)

                                // 토큰이 존재한다면 로그인 성공 처리
                                if (savedToken != null) {

                                    // MainActivity로 이동

                                    findUserData(email) { userName, userID ->
                                        Toast.makeText(this@LoginActivity, "로그인 되었습니다!", Toast.LENGTH_SHORT).show()
                                        // MainActivity로 이동
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.putExtra("userEmail", email)
                                        intent.putExtra("userName", userName)
                                        intent.putExtra("userID", userID)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    Toast.makeText(this@LoginActivity, "토큰 저장 실패!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(this@LoginActivity, "로그인 불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
                }
            })

        }


        binding.signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // GoogleSignInClient 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

//        // 로그인 버튼 클릭 이벤트
//        binding.googleLoginText.setOnClickListener{
//            val signInIntent = mGoogleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//        }

    }

    private fun findUserData(userEmail: String, callback: (String, Int) -> Unit) {
        val retrofit = RetrofitManager.instance

        val sendUserSearch = retrofit.apiService.findUser(userEmail)
        sendUserSearch.enqueue(object : Callback<ResponseAccountDTO> {
            override fun onResponse(
                call: Call<ResponseAccountDTO>,
                response: Response<ResponseAccountDTO>
            ) {
                val responseDto = response.body()
                if (responseDto != null) {
                    val userList = responseDto.userList

                    if (userList.isNotEmpty()) {
                        for (user in userList) {
                            val userName = user.userName.toString()
                            val userID = user.accountID

                            if (userID != null) {
                                callback(userName, userID)
                            }
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No users found for the provided email")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Search Response is null")
                }
            }

            override fun onFailure(call: Call<ResponseAccountDTO>, t: Throwable) {
                Log.e(ContentValues.TAG, "Search Request Failed: ${t.message}", t)
            }
        })
    }
    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)

            // Google 로그인이 성공한 경우
            if (account != null) {
                val userName = account.givenName

                // 여기에서 로그인 성공 처리를 수행하거나 추가적인 동작을 수행할 수 있습니다.
                // 예를 들어, 서버에 Google 계정 정보를 전송하여 사용자를 등록하거나 확인할 수 있습니다.

                // MainActivity로 이동
                //val intent = Intent(this, MainActivity::class.java)
                //intent.putExtra("accountEmail", account.email)
                Log.d("이메일", account.email.toString())
                Log.d("getid", account.id.toString())
                Log.d("idtoken", account.idToken.toString())
                Log.d("serverAuthCode", account.serverAuthCode.toString())
                Log.d("이미지", account.photoUrl.toString())
                Log.d("userName", account.familyName.toString() + userName.toString())
                val name = account.familyName.toString() + userName.toString()

                test(account.email.toString(), name, account.photoUrl.toString())

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("accountEmail", account.email)

                intent.putExtra("userName", userName)

                startActivity(intent)
                finish()
            } else {
                // Google 로그인이 실패한 경우
                Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            // GoogleSignIn.getSignedInAccountFromIntent에서 ApiException이 발생한 경우
            Log.e("GoogleSignIn", "API Exception: ${e.statusCode}")
            Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }

    fun test(email : String, userName : String, imageUrl : String){
        val registerDTO = AccountDTO(imageUrl, userName, email, null, null, null)

        val sendRegister = retrofit.apiService.register(registerDTO)
        sendRegister.enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                val responseDto = response.body()
                if (responseDto != null) {
                    if (responseDto.response) {
                        Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@LoginActivity, "회원가입 불가능!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.e("API TEST", "ERROR  = ${t.message}")
                Toast.makeText(this@LoginActivity, "회원가입불가능 네트워크 에러!", Toast.LENGTH_SHORT).show();
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }
}