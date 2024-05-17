package soongsil.kidbean.front.program.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityProgramEditBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.program.dto.response.ProgramDetailResponse
import soongsil.kidbean.front.program.presentation.ProgramController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient
import java.io.File

class ProgramEditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProgramEditBinding

    private val PERMISSION_REQUEST_CODE = 1

    private var selectedDepartmentPath: String? = null
    private var selectedProgramPath: String? = null

    private val dateList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProgramEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ProgramListActivity::class.java)
            startActivity(intent)
        }

        bottomSetting()

        val programId = intent.getLongExtra("programId", 0)
        loadProgramInfo(programId)

        requestStoragePermission()

        binding.imgProgram.setOnClickListener {
            openGalleryForProgram()
        }

        binding.imgDepartment.setOnClickListener {
            openGalleryForDepart()
        }

        dateButtonSetting()

        //등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEdit.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("프로그램 수정")
                setMessage("프로그램을 수정하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ProgramEditActivity, "수정을 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("수정") { _, _ ->
                    editProgram()
                }
            }.create().show()
        }

        //삭제 버튼 눌렀을 때 팝업 띄우기
        binding.btnProgramDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("프로그램 삭제")
                setMessage("프로그램을 삭제하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ProgramEditActivity, "삭제를 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("삭제") { _, _ ->
                    deleteProgram(programId)
                }
            }.create().show()
        }
    }

    private fun loadProgramInfo(programId: Long) {
        val programController = ApiClient.getApiClient().create(ProgramController::class.java)
        programController.getProgramDetail(programId).enqueue(object :
            Callback<ResponseTemplate<ProgramDetailResponse>> {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
            override fun onResponse(
                call: Call<ResponseTemplate<ProgramDetailResponse>>,
                response: Response<ResponseTemplate<ProgramDetailResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    binding.etProgram.setText(body?.programTitle)

                    Glide.with(this@ProgramEditActivity)
                        .load(body?.departmentS3Url)
                        .into(binding.imgDepartment)

                    binding.imgDepartment.visibility = View.VISIBLE

                    binding.etDepartment.setText(body?.departmentName)
                    binding.etLocation.setText(body?.place)
                    binding.etPhone.setText(body?.phoneNumber)

                    body?.date?.forEach() {
                        when (it) {
                            "월" -> {
                                binding.btnDay1.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("월")
                            }
                            "화" -> {
                                binding.btnDay2.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("화")
                            }
                            "수" -> {
                                binding.btnDay3.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("수")
                            }
                            "목" -> {
                                binding.btnDay4.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("목")
                            }
                            "금" -> {
                                binding.btnDay5.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("금")
                            }
                            "토" -> {
                                binding.btnDay6.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("토")
                            }
                            "일" -> {
                                binding.btnDay7.background = getDrawable(R.drawable.selected_shape_day)
                                dateList.add("일")
                            }
                        }
                    }

                    binding.etContentTitle.setText(body?.contentTitle)

                    Glide.with(this@ProgramEditActivity)
                        .load(body?.programS3Url)
                        .into(binding.imgProgram)

                    binding.etComment.setText(body?.content)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<ProgramDetailResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun deleteProgram(programId: Long) {
        val programController = ApiClient.getApiClient().create(ProgramController::class.java)
        programController.deleteProgram(programId).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(this@ProgramEditActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Toast.makeText(this@ProgramEditActivity, "삭제가 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }

                // ProgramListActivity 이동
                val intent = Intent(this@ProgramEditActivity, ProgramListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun dateButtonSetting() {
        val unClickedBackground = ContextCompat.getDrawable(this, R.drawable.unselected_shape_day)
        val clickedBackground = ContextCompat.getDrawable(this, R.drawable.selected_shape_day)

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

    private fun editProgram() {
        val departmentFileUpdate: MultipartBody.Part = if (!selectedDepartmentPath.isNullOrEmpty()) {
            val imageFile = File(selectedDepartmentPath)
            val fileBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("departmentImage", imageFile.name, fileBody)
        } else {
            val emptyRequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("departmentImage", "", emptyRequestBody)
        }

        val programFileUpdate: MultipartBody.Part = if (!selectedProgramPath.isNullOrEmpty()) {
            val imageFile = File(selectedProgramPath)
            val fileBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("programImage", imageFile.name, fileBody)
        } else {
            val emptyRequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("programImage", "", emptyRequestBody)
        }

        val dateJsonArray = dateList.joinToString(separator = "\", \"", prefix = "[\"", postfix = "\"]")

        val programData = """
        {
            "programId": "${intent.getLongExtra("programId", 0)}",
            "programTitle": "${binding.etProgram.text}",
            "contentTitle": "${binding.etContentTitle.text}",
            "content": "${binding.etComment.text}",
            "date": $dateJsonArray,
            "place": "${binding.etLocation.text}",
            "departmentName": "${binding.etDepartment.text}",
            "phoneNumber": "${binding.tvPhone.text}"
        }
        """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        Log.d("dateList", dateJsonArray)

        val programController = ApiClient.getApiClient().create(ProgramController::class.java)
        programController.editProgram(programFileUpdate, departmentFileUpdate, programData).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(this@ProgramEditActivity, "등록이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Toast.makeText(this@ProgramEditActivity, "등록이 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }

                // MyQuizActivity로 이동
                val intent = Intent(this@ProgramEditActivity, ProgramListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}