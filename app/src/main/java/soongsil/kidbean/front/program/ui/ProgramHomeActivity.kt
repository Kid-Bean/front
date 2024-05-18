package soongsil.kidbean.front.program.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityProgramHomeBinding
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.QuizListActivity

class ProgramHomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProgramHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProgramHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnProgramList.setOnClickListener {
            val intent = Intent(this, ProgramListActivity::class.java)
            startActivity(intent)
        }

        binding.btnStar.setOnClickListener {
            val intent = Intent(this, ProgramStarActivity::class.java)
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
            val intent = Intent(this, ProgramHomeActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지 화면으로 변경하기!
        binding.btnMypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }
}