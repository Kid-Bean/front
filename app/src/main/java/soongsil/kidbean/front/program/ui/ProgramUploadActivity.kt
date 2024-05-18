package soongsil.kidbean.front.program.ui

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityProgramUploadBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.program.presentation.ProgramController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient
import java.io.File

class ProgramUploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProgramUploadBinding

    private val PERMISSION_REQUEST_CODE = 1
    private var category: String = "ACADEMY"

    private var selectedDepartmentPath: String? = null
    private var selectedProgramPath: String? = null

    val dateList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProgramUploadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ProgramListActivity::class.java)
            startActivity(intent)
        }

        bottomSetting()

        requestStoragePermission()

        binding.imgProgram.setOnClickListener {
            openGalleryForProgram()
        }

        binding.imgDepartment.setOnClickListener {
            openGalleryForDepart()
        }

        // 카테고리 세팅
        categorySetting()

        dateButtonSetting()

        //  등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEnroll.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("프로그램 등록")
                setMessage("프로그램을 등록하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ProgramUploadActivity, "등록을 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("등록") { _, _ ->
                    enrollProgram()
                }
            }.create().show()
        }
    }

    private fun dateButtonSetting() {
        val unClickedBackground = ContextCompat.getDrawable(this, soongsil.kidbean.front.R.drawable.unselected_shape_day)
        val clickedBackground = ContextCompat.getDrawable(this, soongsil.kidbean.front.R.drawable.selected_shape_day)

        binding.btnDay1.setOnClickListener {
            if (dateList.contains("월")) {
                dateList.remove("월")
                ViewCompat.setBackground(binding.btnDay1, unClickedBackground)
            } else {
                dateList.add("월")
                ViewCompat.setBackground(binding.btnDay1, clickedBackground)
            }
        }

        binding.btnDay2.setOnClickListener {
            if (dateList.contains("화")) {
                dateList.remove("화")
                ViewCompat.setBackground(binding.btnDay2, unClickedBackground)
            } else {
                dateList.add("화")
                ViewCompat.setBackground(binding.btnDay2, clickedBackground)
            }
        }

        binding.btnDay3.setOnClickListener {
            if (dateList.contains("수")) {
                dateList.remove("수")
                ViewCompat.setBackground(binding.btnDay3, unClickedBackground)
            } else {
                dateList.add("수")
                ViewCompat.setBackground(binding.btnDay3, clickedBackground)
            }
        }

        binding.btnDay4.setOnClickListener {
            if (dateList.contains("목")) {
                dateList.remove("목")
                ViewCompat.setBackground(binding.btnDay4, unClickedBackground)
            } else {
                dateList.add("목")
                ViewCompat.setBackground(binding.btnDay4, clickedBackground)
            }
        }

        binding.btnDay5.setOnClickListener {
            if (dateList.contains("금")) {
                dateList.remove("금")
                ViewCompat.setBackground(binding.btnDay5, unClickedBackground)
            } else {
                dateList.add("금")
                ViewCompat.setBackground(binding.btnDay5, clickedBackground)
            }
        }

        binding.btnDay6.setOnClickListener {
            if (dateList.contains("토")) {
                dateList.remove("토")
                ViewCompat.setBackground(binding.btnDay6, unClickedBackground)
            } else {
                dateList.add("토")
                ViewCompat.setBackground(binding.btnDay6, clickedBackground)
            }
        }

        binding.btnDay7.setOnClickListener {
            if (dateList.contains("일")) {
                dateList.remove("일")
                ViewCompat.setBackground(binding.btnDay7, unClickedBackground)
            } else {
                dateList.add("일")
                ViewCompat.setBackground(binding.btnDay7, clickedBackground)
            }
        }
    }

    private fun bottomSetting() {
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 문제 풀기 화면으로 변경하기!
        binding.btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            startActivity(intent)
        }

        // 프로그램 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            val intent = Intent(this, ProgramHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private const val REQUEST_DEPART_IMAGE_PICK = 1
        private const val REQUEST_PROGRAM_IMAGE_PICK = 2
    }

    private fun openGalleryForDepart() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_DEPART_IMAGE_PICK)
    }

    private fun openGalleryForProgram() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PROGRAM_IMAGE_PICK)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("activity", "${requestCode}, ${resultCode}, ${data}")
        if (requestCode == REQUEST_DEPART_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            binding.imgDepartment.setImageURI(imageUri)
            selectedDepartmentPath = imageUri?.let { getImagePath(it) }
            selectedDepartmentPath?.let { Log.d("getpath", it) }
        } else if (requestCode == REQUEST_PROGRAM_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            binding.imgProgram.setImageURI(imageUri)
            selectedProgramPath = imageUri?.let { getImagePath(it) }
            selectedProgramPath?.let { Log.d("getpath", it) }
        }
    }

    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun enrollProgram() {
        val departmentFileUpdate: MultipartBody.Part? = if (!selectedDepartmentPath.isNullOrEmpty()) {
            val imageFile = File(selectedDepartmentPath)
            val fileBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("departmentImage", imageFile.name, fileBody)
        } else {
            null
        }

        val programFileUpdate: MultipartBody.Part? = if (!selectedProgramPath.isNullOrEmpty()) {
            val imageFile = File(selectedProgramPath)
            val fileBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("programImage", imageFile.name, fileBody)
        } else {
            null
        }

        val dateJsonArray = dateList.joinToString(separator = "\", \"", prefix = "[\"", postfix = "\"]")

        val programData = """
        {
            "programTitle": "${binding.etProgram.text}",
            "contentTitle": "${binding.etContentTitle.text}",
            "programCategory": "$category",
            "content": "${binding.etComment.text}",
            "date": $dateJsonArray,
            "place": "${binding.etLocation.text}",
            "departmentName": "${binding.etDepartment.text}",
            "phoneNumber": "${binding.tvPhone.text}"
        }
        """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        Log.d("dateList", dateJsonArray)

        val programController = ApiClient.getApiClient().create(ProgramController::class.java)
        if (departmentFileUpdate != null && programFileUpdate != null) {
            programController.uploadProgram(programFileUpdate!!, departmentFileUpdate, programData).enqueue(object :
                Callback<ResponseTemplate<Void>> {
                override fun onResponse(
                    call: Call<ResponseTemplate<Void>>,
                    response: Response<ResponseTemplate<Void>>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString())
                        Toast.makeText(this@ProgramUploadActivity, "등록이 완료되었습니다.", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Toast.makeText(this@ProgramUploadActivity, "등록이 실패하였습니다.", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("post", "onResponse 실패 + ${response.code()}")
                    }

                    // MyQuizActivity로 이동
                    val intent = Intent(this@ProgramUploadActivity, ProgramListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

                override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
        } else {
            // 파일이 선택되지 않았을 때 처리할 로직 추가 가능
            Toast.makeText(this@ProgramUploadActivity, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun categorySetting() {
        // 스피너에 표시될 데이터 생성
        val categories: MutableList<String> = ArrayList()
        categories.add("학습")
        categories.add("병원")

        // 어댑터 생성 및 데이터 설정
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        val spinner = binding.tvCategory
        spinner.adapter = adapter

        // 스피너 아이템 선택 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // 선택된 아이템의 텍스트 가져오기
                val selectedCategory = parent.getItemAtPosition(position).toString()

                if (selectedCategory.equals("학습")) {
                    category = "ACADEMY"
                } else if (selectedCategory.equals("병원")) {
                    category = "HOSPITAL"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때 처리할 작업 (필요에 따라 구현)
            }
        }
    }
}