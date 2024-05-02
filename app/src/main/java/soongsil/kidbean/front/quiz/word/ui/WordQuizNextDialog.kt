package soongsil.kidbean.front.quiz.word.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityWordQuizNextDialogBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveResponse
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizSolveListRequest
import soongsil.kidbean.front.quiz.word.dto.request.WordQuizSolveRequest
import soongsil.kidbean.front.quiz.word.dto.response.WordQuizSolveScoreResponse
import soongsil.kidbean.front.quiz.word.presentation.WordQuizController
import soongsil.kidbean.front.util.ApiClient
import java.io.Serializable

class WordQuizNextDialog : AppCompatActivity() {

    private lateinit var binding : ActivityWordQuizNextDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordQuizNextDialogBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        ApiClient.init(this)

        val quizCount = intent.getLongExtra("quizCount", 1)
        val originalList = intent.getSerializableExtra("listData") as? MutableList<Pair<Long, String>> ?: mutableListOf()
        val quizList: ArrayList<ImageQuizSolveResponse>? = intent.getParcelableArrayListExtra("quizList")
        var score = 0L

        //3번째 문제 - 푼 문제들을 전부 제출
        if (quizCount == 3L) {
            val quizSolvedRequestList = originalList.map { (key, value) ->
                WordQuizSolveRequest(quizId = key, answer = value)
            }

            val request = WordQuizSolveListRequest(quizSolvedRequestList = quizSolvedRequestList)

            val wordQuizController = ApiClient.getApiClient().create(WordQuizController::class.java)

            wordQuizController.solveWordQuiz(request).enqueue(object :
                Callback<ResponseTemplate<WordQuizSolveScoreResponse>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseTemplate<WordQuizSolveScoreResponse>>,
                    response: Response<ResponseTemplate<WordQuizSolveScoreResponse>>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString())

                        val body = response.body()?.results

                        Log.d("score", score.toString())

                        // API로 가져온 정답 넣기
                        score = body?.score!!
                        Log.d("score", score.toString())

                        binding.tvTitle.text = "3문제를 전부 풀었습니다!\n얻은 점수 : $score"
                        binding.btnNext.text = "홈 화면으로"
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패 + ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseTemplate<WordQuizSolveScoreResponse>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })

            binding.btnNext.setOnClickListener {
                //점수를 가지고 Home 화면으로 이동
                val intent = Intent(this@WordQuizNextDialog, QuizListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("score", score)
                // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
        } else {
            binding.btnNext.setOnClickListener {
                val intent = Intent(this@WordQuizNextDialog, WordQuizSolveActivity::class.java)
                // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                intent.putExtra("listData", originalList as Serializable) // Map을 Serializable로 캐스팅
                intent.putExtra("quizCount", quizCount + 1L)
                intent.putParcelableArrayListExtra("quizList", ArrayList(quizList))

                startActivity(intent)
                finish()
            }
        }
    }

    @Override
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event!!.action != MotionEvent.ACTION_OUTSIDE
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
    }
}