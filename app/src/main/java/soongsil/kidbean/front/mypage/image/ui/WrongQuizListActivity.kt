package soongsil.kidbean.front.mypage.image.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityWrongImageQuizSolvedListBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MySolvedQuizActivity
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageListResponse
import soongsil.kidbean.front.mypage.presentation.MypageController
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient

class WrongQuizListActivity : AppCompatActivity(){
    private lateinit var binding: ActivityWrongImageQuizSolvedListBinding

    private lateinit var quizList : List<SolvedImageListResponse.SolvedListInfo>

    private var selectCategory = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWrongImageQuizSolvedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnAnimal.setOnClickListener { loadQuizListFilter("ANIMAL") }
        binding.btnPlant.setOnClickListener { loadQuizListFilter("PLANT") }
        binding.btnObject.setOnClickListener { loadQuizListFilter("OBJECT") }
        binding.btnOther.setOnClickListener { loadQuizListFilter("NONE") }
        binding.btnFood.setOnClickListener { loadQuizListFilter("FOOD") }

        loadQuizList()
        bottomSetting()

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MyQuizActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setAdapter(quizList: List<SolvedImageListResponse.SolvedListInfo>) {
        val listAdapter = QuizListAdapter(quizList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvQuiz.adapter = listAdapter
        binding.rvQuiz.layoutManager = linearLayoutManager
        binding.rvQuiz.setHasFixedSize(true)

        changeButtonColor()
    }

    private fun changeButtonColor() {
        val buttonIds = mapOf(
            "ANIMAL" to R.id.btn_animal,
            "PLANT" to R.id.btn_plant,
            "OBJECT" to R.id.btn_object,
            "NONE" to R.id.btn_other,
            "FOOD" to R.id.btn_food
        )

        val selectedColor = Color.parseColor("#4CAF50")
        val defaultColor = Color.parseColor("#69F0AE")

        buttonIds.forEach { (categoryName, buttonId) ->
            val button = findViewById<Button>(buttonId)
            button.backgroundTintList = if (categoryName == selectCategory) {
                ColorStateList.valueOf(selectedColor)
            } else {
                ColorStateList.valueOf(defaultColor)
            }
        }
    }

    private fun loadQuizListFilter(category: String) {
        if (category == selectCategory) {
            selectCategory = "all"
            setAdapter(quizList)
        } else {
            selectCategory = category
            val filteredList = quizList.filter { it.quizCategory == category }
            setAdapter(filteredList)
        }
    }


    private fun loadQuizList() {
        val myPageController =
            ApiClient.getApiClient().create(MypageController::class.java)
        myPageController.getImageQuizList(false).enqueue(object :
            Callback<ResponseTemplate<SolvedImageListResponse>> {
            override fun onResponse(
                call: Call<ResponseTemplate<SolvedImageListResponse>>,
                response: Response<ResponseTemplate<SolvedImageListResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")
                    quizList = body.solvedList
                    setAdapter(body.solvedList)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<ResponseTemplate<SolvedImageListResponse>>,
                t: Throwable
            ) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }

        })
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
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MySolvedQuizActivity::class.java)
            startActivity(intent)
        }
    }

}
