package soongsil.kidbean.front.quiz.image.ui

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
import soongsil.kidbean.front.databinding.ActivityImageQuizNextDialogBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizSolveListRequest
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizSolveRequest
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveResponse
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveScoreResponse
import soongsil.kidbean.front.quiz.image.presentation.ImageQuizController
import soongsil.kidbean.front.util.ApiClient
import java.io.Serializable

class ImageQuizNextDialog : AppCompatActivity() {

    private lateinit var binding : ActivityImageQuizNextDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageQuizNextDialogBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        ApiClient.init(this)

        val quizCount = intent.getLongExtra("quizCount", 1)
        val originalList = intent.getSerializableExtra("listData") as? MutableList<Pair<Long, String>> ?: mutableListOf()
        val quizList: ArrayList<ImageQuizSolveResponse>? = intent.getParcelableArrayListExtra("quizList")
        val result = intent.getStringExtra("result")
        var score = 0L

        Log.d("quiz count in dialog", quizCount.toString())

        //5번째 문제 - 푼 문제들을 전부 제출
        if (quizCount == 5L) {
            val quizSolveRequestList = originalList.map { (key, value) ->
                ImageQuizSolveRequest(quizId = key, answer = value)
            }

            val request = ImageQuizSolveListRequest(quizSolvedRequestList = quizSolveRequestList)

            val imageQuizController = ApiClient.getApiClient().create(ImageQuizController::class.java)
            imageQuizController.solveImageQuiz(request).enqueue(object :
                Callback<ResponseTemplate<ImageQuizSolveScoreResponse>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseTemplate<ImageQuizSolveScoreResponse>>,
                    response: Response<ResponseTemplate<ImageQuizSolveScoreResponse>>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString())

                        val body = response.body()?.results

                        // API로 가져온 정답 넣기
                        score = body?.score!!
                        Log.d("score", score.toString())

                        binding.tvTitle.text = "5문제를 전부 풀었습니다!\n얻은 점수 : $score"
                        binding.btnNext.text = "홈 화면으로"
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패 + ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseTemplate<ImageQuizSolveScoreResponse>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })

            binding.btnNext.setOnClickListener {
                //점수를 가지고 Home 화면으로 이동
                val intent = Intent(this@ImageQuizNextDialog, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("score", score)
                // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
        } else {

            binding.tvTitle.text = "입력한 정답\n$result"

            binding.btnNext.setOnClickListener {
                val intent = Intent(this@ImageQuizNextDialog, ImageQuizSolveActivity::class.java)
                // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                intent.putExtra("listData", originalList as Serializable) // Map을 Serializable로 캐스팅
                intent.putExtra("quizCount", quizCount + 1)
                // 서버에서 받은 데이터 리스트 전달
                intent.putParcelableArrayListExtra("quizList", ArrayList(quizList))
                // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

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