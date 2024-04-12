package soongsil.kidbean.front.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityImageQuizListBinding
import soongsil.kidbean.front.databinding.ActivityMyQuizBinding
import soongsil.kidbean.front.quiz.image.ui.ImageQuizListActivity

class MyQuizActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyQuizBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnImage.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, ImageQuizListActivity::class.java)
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
            /*val intent = Intent(this, ImageQuizShowActivity::class.java)
            startActivity(intent)*/
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