package soongsil.kidbean.front.quiz.image.ui

import RetrofitImpl.retrofit
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.CaseMap.Title
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityImageQuizUpdateBinding
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizUpdateRequest
import soongsil.kidbean.front.quiz.image.presentation.ImageQuizController
import java.io.File

class ImageQuizUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageQuizUpdateBinding
    private lateinit var title: String
    private lateinit var answer: String

    private val PERMISSION_REQUEST_CODE = 1
    private var selectedImagePath: String? = null

    private var category: String = "ANIMAL"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImageQuizUpdateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, ImageQuizShowActivity::class.java)
            startActivity(intent)
        }

        title = intent.getStringExtra("title").toString()
        answer = intent.getStringExtra("answer").toString()

        binding.tvTitle.setText(title)
        binding.tvCorrect.setText(answer)

        val imageView: ImageView = binding.imgQuiz
        Glide.with(this@ImageQuizUpdateActivity)
            .load(intent.getStringExtra("imgUrl"))
            .into(imageView)
        imageView.visibility = View.VISIBLE

        binding.imgQuiz.setOnClickListener {
            //갤러리 호출
            requestStoragePermission()

            openGallery()
        }

        // 카테고리 세팅
        categorySetting()

        //  등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEnroll.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 등록")
                setMessage("문제를 등록하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ImageQuizUpdateActivity, "등록을 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("삭제") { _, _ ->
                    loadInfo()

                    Toast.makeText(this@ImageQuizUpdateActivity, "등록이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }.create().show()

            finish()
        }
    }

    private fun openGallery() {
        //갤러리 호출
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ImageQuizUpdateActivity.REQUEST_IMAGE_PICK)
    }

    private fun requestStoragePermission() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    //결과 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        //결과 코드 OK , 결가값 null 아니면
        if(it.resultCode == RESULT_OK && it.data != null){
            //값 담기
            val uri  = it.data!!.data

            //화면에 보여주기
            Glide.with(this)
                .load(uri) //이미지
                .into(binding.imgQuiz) //보여줄 위치
        }
    }

    //결과 가져오기
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageQuizUpdateActivity.REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            binding.imgQuiz.setImageURI(imageUri)
            selectedImagePath = imageUri?.let { getImagePath(it) }
            selectedImagePath?.let { Log.d("path", it) }
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

    fun loadInfo() {
        val fileUpdate: MultipartBody.Part? = if (!selectedImagePath.isNullOrEmpty()) {
            val imageFile = File(selectedImagePath)
            val fileBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", imageFile.name, fileBody)
        } else {
            null
        }

        if (binding.tvCategory.equals("식물")) {
            category = "PLANT"
        }

        val quizData = """
        {
            "title": ${binding.tvTitle.text},
            "answer": ${binding.tvCorrect.text},
            "category": ${category}
        }
        """.trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

        val imageQuizController = retrofit.create(ImageQuizController::class.java)
        if (fileUpdate != null) {
            imageQuizController.updateImageQuiz(1, 7, fileUpdate, quizData).enqueue(object :
                Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString())

                        val body = response.body()

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패 + ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
        } else {
            // 파일이 선택되지 않았을 때 처리할 로직 추가 가능
            Toast.makeText(this@ImageQuizUpdateActivity, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadInfo()
    }

    override fun onResume() {
        super.onResume()
        loadInfo()
    }

    private fun categorySetting() {
        // 스피너에 표시될 데이터 생성
        val categories: MutableList<String> = ArrayList()
        categories.add("없음")
        categories.add("동물")
        categories.add("식물")
        categories.add("음식")

        // 어댑터 생성 및 데이터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

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
                // 선택된 아이템에 대한 작업 수행 (예: 토스트 메시지 표시)
                Toast.makeText(
                    this@ImageQuizUpdateActivity,
                    "선택된 카테고리: $selectedCategory",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때 처리할 작업 (필요에 따라 구현)
            }
        }
    }
}