package soongsil.kidbean.front.quiz

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityQuizListBinding
import soongsil.kidbean.front.quiz.answer.ui.AnswerQuizSolveActivity
import soongsil.kidbean.front.quiz.image.ui.ImageQuizSolveActivity
import soongsil.kidbean.front.quiz.word.ui.WordQuizSolveActivity

class QuizListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQuizListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnImage.setOnClickListener {
            val intent = Intent(this, ImageQuizSolveActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnWord.setOnClickListener {
            val intent = Intent(this, WordQuizSolveActivity::class.java)
            startActivity(intent)
        }

        binding.btnAnswer.setOnClickListener {
            val intent = Intent(this, AnswerQuizSolveActivity::class.java)
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
        binding.btnProgram.setOnClickListener {
            /*val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)*/
        }
    }
}