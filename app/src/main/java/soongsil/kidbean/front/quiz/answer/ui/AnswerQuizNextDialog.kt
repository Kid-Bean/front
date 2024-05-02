package soongsil.kidbean.front.quiz.answer.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import soongsil.kidbean.front.databinding.ActivityAnswerQuizNextDialogBinding
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveResponse
import soongsil.kidbean.front.util.ApiClient

class AnswerQuizNextDialog : AppCompatActivity() {

    private lateinit var binding : ActivityAnswerQuizNextDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerQuizNextDialogBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val score = intent.getLongExtra("score", 1)
        val result = intent.getStringExtra("result")

        binding.tvTitle.text = "나의 대답\n$result"

        binding.btnNext.setOnClickListener {
            //점수를 가지고 Home 화면으로 이동
            val intent = Intent(this@AnswerQuizNextDialog, QuizListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("score", score)
            // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}