package soongsil.kidbean.front.quiz.answer.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AlertDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityAnswerQuizNextDialogBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveScoreResponse
import soongsil.kidbean.front.quiz.answer.presentation.AnswerQuizController
import soongsil.kidbean.front.util.ApiClient
import java.io.File

class AnswerQuizNextDialog : AppCompatActivity() {

    private lateinit var binding : ActivityAnswerQuizNextDialogBinding

    private var score = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerQuizNextDialogBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        ApiClient.init(this)

        val sessionId = intent.getStringExtra("sessionId")
        val quizId = intent.getStringExtra("quizId")
        val answer = intent.getStringExtra("answer")
        val filePath = intent.getStringExtra("filePath")

        val file = File(filePath, "$sessionId.pcm")
        val requestFile = file.asRequestBody("audio/pcm".toMediaTypeOrNull())
        val recordPart = MultipartBody.Part.createFormData("record", file.name, requestFile)

        binding.tvTitle.text = "입력한 대답\n$answer"
        binding.btnNext.text = "홈 화면으로"

        val quizData = """
            {
                "quizId": "$quizId",
                "answer": "${answer.toString()}"
            }
            """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val quizDataPart = MultipartBody.Part.createFormData("answerQuizSolvedRequest", "", quizData)

        val answerQuizController = ApiClient.getApiClient().create(AnswerQuizController::class.java)
        answerQuizController.solveAnswerQuiz(recordPart, quizDataPart).enqueue(object :
            Callback<ResponseTemplate<AnswerQuizSolveScoreResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseTemplate<AnswerQuizSolveScoreResponse>>,
                response: Response<ResponseTemplate<AnswerQuizSolveScoreResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    // API로 가져온 정답 넣기
                    score = body?.score!!

                    Log.d("score", score.toString())

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<AnswerQuizSolveScoreResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })

        binding.btnNext.setOnClickListener {
            //점수를 가지고 Home 화면으로 이동
            val intent = Intent(this@AnswerQuizNextDialog, QuizListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("score", score)
            startActivity(intent)
            finish()
        }
    }
}