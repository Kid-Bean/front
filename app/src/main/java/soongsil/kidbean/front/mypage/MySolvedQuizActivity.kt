package soongsil.kidbean.front.mypage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import soongsil.kidbean.front.MainActivity
import soongsil.kidbean.front.databinding.ActivityMySolvedQuizMainBinding
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.QuizListActivity

class MySolvedQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMySolvedQuizMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySolvedQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSetting()

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, MyQuizActivity::class.java)
            startActivity(intent)
        }

        binding.btnImage.setOnClickListener {
            // 이미지 퀴즈 메인
            val intent = Intent(this, SolvedImageQuizMainActivity::class.java)
            startActivity(intent)
        }

        binding.btnWord.setOnClickListener {
            //단어 퀴즈 메인
//            val intent = Intent(this, MySolvedQuizActivity::class.java)
//            startActivity(intent)
        }

        binding.btnVoice.setOnClickListener {
            //음성 퀴즈 메인
//            val intent = Intent(this, MyQuizActivity::class.java)
//            startActivity(intent)
        }
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