package com.example.frontsharestorage.User

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.frontsharestorage.Account.AccountDTO
import com.example.frontsharestorage.Account.ResponseAccountDTO
import com.example.frontsharestorage.Account.UserRankingDTO
import com.example.frontsharestorage.DTO.RetrofitManager
import com.example.frontsharestorage.databinding.ActivitySignupBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var isEmailValid = false
    private var isDuplicateEmail = false

    private val PERMISSION_REQUEST_CODE = 203
    private val CAMERA_REQUEST_CODE = 204
    private val GALLERY_REQUEST_CODE = 206
    private var URL = ""

    private var currentPhotoPath: String? = null
    private val storageRef = FirebaseStorage.getInstance().reference

    private lateinit var signupProfileImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitManager.instance

        signupProfileImage = binding.signupProfileImage

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

            if (emailEditText.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "이메일을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val sendValidCheck = retrofit.apiService.emailValidCheck(emailEditText);

                sendValidCheck.enqueue(object : Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>
                    ) {
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
                            Toast.makeText(this@SignUpActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
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
                Toast.makeText(this@SignUpActivity, "필수 항목을 모두 작성 해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // 중복 체크 여부 확인
            if (!isEmailValid) {
                Toast.makeText(this@SignUpActivity, "중복 체크를 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerDTO = AccountDTO(URL, userName, email, userNickName, userPhone, password)

            val sendRegister = retrofit.apiService.register(registerDTO)
            sendRegister.enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    val responseDto = response.body()
                    if (responseDto != null) {
                        if (responseDto.response) {
                            Toast.makeText(this@SignUpActivity, "회원가입 되었습니다!", Toast.LENGTH_SHORT).show()

                            findUserData(email) { userID ->
                                val userRankingDTO = UserRankingDTO(userID, email, 0, "브론즈", userNickName, URL)
                                val sendAddRanking = retrofit.apiService.addRanking(userRankingDTO)
                                sendAddRanking.enqueue(object : Callback<ResponseDTO> {
                                    override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                                        val responseDto = response.body()
                                        if (responseDto != null) {
                                            if (responseDto.response) {
                                                Log.d("addRanking", "추가 완료")
                                            }
                                        }
                                    }
                                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                                        Toast.makeText(this@SignUpActivity, "회원가입불가능 네트워크 에러!", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                            finish()
                        } else {
                            Toast.makeText(this@SignUpActivity, "회원가입 불가능!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "서버 응답이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("API TEST", "ERROR  = ${t.message}")
                    Toast.makeText(this@SignUpActivity, "회원가입불가능 네트워크 에러!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }

    binding.signupCameraButton.setOnClickListener {
            checkCameraPermission()
        }
        binding.signupGalleryButton.setOnClickListener {
            openGallery()
        }
    }

    private fun findUserData(userEmail: String, callback: (Int) -> Unit) {
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
                            val userID = user.accountID

                            if (userID != null) {
                                callback(userID)
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

    private fun enableNextStepButton() {
        val nextStepButton = binding.signupSaveButtonFrame
        nextStepButton.isEnabled = isEmailValid && isDuplicateEmail
    }

    private fun checkCameraPermission() {
        // 카메라 권한이 있는지 확인하고 없는 경우 권한 요청
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        // 카메라 앱을 열어 사진 촬영
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this@SignUpActivity,
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
        val storageDir: File? = this@SignUpActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // 파일 경로 저장
            currentPhotoPath = absolutePath
        }
    }

    // onActivityResult 함수에 다음과 같이 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    // 갤러리에서 이미지를 선택한 경우
                    data?.data?.let { selectedImageUri ->
                        // 선택한 이미지를 Firebase에 업로드
                        uploadImageToFirebaseStorage(selectedImageUri)
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    // 카메라로 찍은 이미지를 Firebase에 업로드
                    uploadImageToFirebaseStorage(Uri.fromFile(File(currentPhotoPath)))
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val imagesRef: StorageReference = storageRef.child("images/${imageUri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d("Firebase Storage", "Image Upload Success. Download URL: ${downloadUri.toString()}")
                URL = downloadUri.toString()

                // 이미지를 signupProfileImage ImageView에 표시
                Glide.with(this)
                    .load(imageUri)
                    .override(120, 120)
                    .centerCrop()
                    .circleCrop()
                    .into(binding.signupProfileImage)
            } else {
                // 이미지 업로드 실패 처리
                Log.e("Firebase Storage", "Image Upload Failed")
            }
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


}