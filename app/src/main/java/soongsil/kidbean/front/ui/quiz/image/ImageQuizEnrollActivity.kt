package soongsil.kidbean.front.ui.quiz.image

import RetrofitImpl.retrofit
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.R
import soongsil.kidbean.front.data.controller.ImageQuizController
import soongsil.kidbean.front.databinding.ActivityImageQuizEnrollBinding

class ImageQuizEnrollActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageQuizEnrollBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImageQuizEnrollBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            // 그림 문제 목록 화면으로 이동
            val intent = Intent(this, ImageQuizShowActivity::class.java)
            startActivity(intent)
        }

        if (intent.hasExtra("edit")) {
            binding.tvTitle.setText(intent.getStringExtra("title"))
            binding.tvCorrect.setText(intent.getStringExtra("answer"))

            val imageView: ImageView = binding.imgQuiz
            Glide.with(this@ImageQuizEnrollActivity)
                .load(intent.getStringExtra("imgUrl"))
                .into(imageView)
            imageView.visibility = View.VISIBLE
        }

        //loadInfo()

        binding.imgQuiz.setOnClickListener {
            //갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        // 카테고리 세팅
        categorySetting()

        //  등록 버튼 눌렀을 때 팝업 띄우기
        binding.btnEnroll.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("그림 문제 등록")
                setMessage("문제를 등록하시겠습니까?")
                setNegativeButton("취소") { _, _ ->
                    Toast.makeText(this@ImageQuizEnrollActivity, "등록을 취소하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                setPositiveButton("삭제") { _, _ ->
                    Toast.makeText(this@ImageQuizEnrollActivity, "등록이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }.create().show()

            finish()
        }
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


    /*private fun selectGallery(){
        progressOn()
        val readPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if(readPermission == PackageManager.PERMISSION_DENIED) { //권한이 없을 시 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY
            )
        }else{
            //권한이 있을 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*")
            imageResult.launch(intent)
        }
    }*/

    private fun loadInfo() {
        val imageQuizController = retrofit.create(ImageQuizController::class.java)
        imageQuizController.getImageQuizById(1, 2).enqueue(object :
            Callback<ImageQuizMemberDetailResponse> {
            override fun onResponse(
                call: Call<ImageQuizMemberDetailResponse>,
                response: Response<ImageQuizMemberDetailResponse>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()

                    // API로 가져온 제목 넣기
                    binding.tvTitle.text = body?.title

                    // API로 가져온 이미지 넣기
                    val imageView: ImageView = binding.imgQuiz
                    val imgUrl = body?.imgUrl

                    Glide.with(this@ImageQuizEnrollActivity)
                        .load(imgUrl)
                        .into(imageView)
                    imageView.visibility = View.VISIBLE

                    // API로 가져온 정답 넣기
                    binding.tvCorrect.text = body?.answer

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ImageQuizMemberDetailResponse>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }*/

    override fun onRestart() {
        super.onRestart()
        //loadInfo()
    }

    override fun onResume() {
        super.onResume()
        //loadInfo()
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
                    this@ImageQuizEnrollActivity,
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