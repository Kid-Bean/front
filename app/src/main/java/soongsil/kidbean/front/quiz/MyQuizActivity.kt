package soongsil.kidbean.front.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityMyQuizBinding
import soongsil.kidbean.front.quiz.answer.ui.AnswerQuizListActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.image.ui.ImageQuizListActivity
import soongsil.kidbean.front.quiz.word.ui.WordQuizListActivity
import soongsil.kidbean.front.util.ApiClient

class MyQuizActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyQuizBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnImage.setOnClickListener {
            val intent = Intent(this, ImageQuizListActivity::class.java)
            startActivity(intent)
        }

        binding.btnWord.setOnClickListener {
            val intent = Intent(this, WordQuizListActivity::class.java)
            startActivity(intent)
        }

        binding.btnAnswer.setOnClickListener {
            val intent = Intent(this, AnswerQuizListActivity::class.java)
            startActivity(intent)
        }

        bottomSetting()
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
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }
}