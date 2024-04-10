package soongsil.kidbean.front.quiz.answer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityAnswerQuizShowBinding

class AnswerQuizShowActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAnswerQuizShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_quiz_show)
    }
}