package soongsil.kidbean.front.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityMyImageQuizSolvedMainBinding
import soongsil.kidbean.front.databinding.ActivityRightImageQuizSolvedListBinding
import soongsil.kidbean.front.databinding.ActivityWrongImageQuizSolvedListBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.image.dto.response.MyPageImageScoreResponse
import soongsil.kidbean.front.mypage.image.dto.response.SolvedImageListResponse
import soongsil.kidbean.front.mypage.image.presentation.MypageImageController
import soongsil.kidbean.front.mypage.image.ui.QuizListAdapter
import soongsil.kidbean.front.mypage.main.dto.QuizCategory
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizMemberResponse
import soongsil.kidbean.front.quiz.answer.ui.AnswerQuizAdapter
import soongsil.kidbean.front.util.ApiClient

class WrongQuizListActivity : AppCompatActivity(){
    private lateinit var binding: ActivityWrongImageQuizSolvedListBinding

    private lateinit var quizList : List<SolvedImageListResponse.SolvedListInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWrongImageQuizSolvedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnAnimal.setOnClickListener { loadQuizListFilter("ANIMAL") }
        binding.btnPlant.setOnClickListener { loadQuizListFilter("PLANT") }
        binding.btnObject.setOnClickListener { loadQuizListFilter("OBJECT") }
        binding.btnOther.setOnClickListener { loadQuizListFilter("NONE") }

        loadQuizList()
        bottomSetting()

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MyQuizActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setAdapter(quizList: List<SolvedImageListResponse.SolvedListInfo>, category: String) {
        val listAdapter = QuizListAdapter(quizList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvQuiz.adapter = listAdapter
        binding.rvQuiz.layoutManager = linearLayoutManager
        binding.rvQuiz.setHasFixedSize(true)

        changeButtonColor(category)
    }

    private fun changeButtonColor(category: String) {
        val btnAnimal = findViewById<Button>(R.id.btn_animal)
        val btnPlant = findViewById<Button>(R.id.btn_plant)
        val btnObject = findViewById<Button>(R.id.btn_object)
        val btnOther = findViewById<Button>(R.id.btn_other)

        if (category == "all") {
            btnAnimal.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#69F0AE"))
            btnPlant.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#69F0AE"))
            btnObject.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#69F0AE"))
            btnOther.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#69F0AE"))
        }
        btnAnimal.backgroundTintList = if (category == "ANIMAL") ColorStateList.valueOf(Color.parseColor("#4CAF50")) else ColorStateList.valueOf(Color.parseColor("#69F0AE"))
        btnPlant.backgroundTintList = if (category == "PLANT") ColorStateList.valueOf(Color.parseColor("#4CAF50")) else ColorStateList.valueOf(Color.parseColor("#69F0AE"))
        btnObject.backgroundTintList = if (category == "OBJECT") ColorStateList.valueOf(Color.parseColor("#4CAF50")) else ColorStateList.valueOf(Color.parseColor("#69F0AE"))
        btnOther.backgroundTintList = if (category == "NONE") ColorStateList.valueOf(Color.parseColor("#4CAF50")) else ColorStateList.valueOf(Color.parseColor("#69F0AE"))
    }

    private fun loadQuizListFilter(category: String) {
        val filteredList = quizList.filter { it.quizCategory == category }
        setAdapter(filteredList, category)
    }


    private fun loadQuizList() {
        val myPageImageController =
            ApiClient.getApiClient().create(MypageImageController::class.java)
        myPageImageController.getImageQuizList(false).enqueue(object :
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
                    setAdapter(body.solvedList, "all")
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
