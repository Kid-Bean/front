package soongsil.kidbean.front.quiz

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityQuizListBinding
import soongsil.kidbean.front.quiz.image.ui.ImageQuizSolveActivity

class QuizListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQuizListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 인텐트에서 액세스 토큰을 추출합니다.
        val accessToken = intent.getStringExtra("ACCESS_TOKEN")
        if (accessToken != null) {
            // 여기에서 토큰을 사용한 로직을 구현합니다.
            Log.i(TAG, "받은 액세스 토큰: $accessToken")
        }
        // 인텐트에서 액세스 토큰을 추출합니다.
        val refreshToken = intent.getStringExtra("REFRESH_TOKEN")
        if (refreshToken != null) {
            // 여기에서 토큰을 사용한 로직을 구현합니다.
            Log.i(TAG, "받은 액세스 토큰: $refreshToken")
        }

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
            /*val intent = Intent(this, WordQuizSolveActivity::class.java)
            startActivity(intent)*/
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