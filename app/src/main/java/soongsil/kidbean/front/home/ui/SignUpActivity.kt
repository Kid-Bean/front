package soongsil.kidbean.front.home.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityMainBinding
import soongsil.kidbean.front.databinding.ActivitySignUpBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.dto.request.MemberInfoRequest
import soongsil.kidbean.front.home.dto.response.HomeResponse
import soongsil.kidbean.front.home.presentation.HomeController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var gender : String
    private lateinit var createDate : LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.init(this)

        val btnBoy = binding.btnBoy
        val btnGirl = binding.btnGirl

        btnBoy.setOnClickListener {
            updateButtonStyles(btnBoy, btnGirl)
            gender = "MAN"
        }

        btnGirl.setOnClickListener {
            updateButtonStyles(btnGirl, btnBoy)
            gender = "WOMAN"
        }



        binding.btnEnroll.setOnClickListener {
            loadInfo()

            // 단어 문제 등록 화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateButtonStyles(selectedButton: Button, otherButton: Button) {
        // 선택된 버튼
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green)) // 선택된 상태의 배경색으로 변경
        selectedButton.setTextColor(ContextCompat.getColor(this, R.color.white)) // 선택된 상태의 텍스트 색으로 변경

        // 선택되지 않은 다른 버튼
        otherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green100)) // 기본 배경색으로 변경
        otherButton.setTextColor(ContextCompat.getColor(this, R.color.black)) // 기본 텍스트 색으로 변경
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendDateToServer() {
        // EditText에서 입력된 값을 가져옵니다.
        val year = binding.etYear.text.toString()
        val month = binding.etMonth.text.toString().padStart(2, '0') // 항상 두 자리 수가 되도록 함
        val day = binding.etDay.text.toString().padStart(2, '0') // 항상 두 자리 수가 되도록 함

        // "yyyy-MM-dd" 형식의 문자열 생성
        val dateString = "$year-$month-$day"
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        createDate = LocalDate.parse(dateString, dateFormatter)
    }

    private fun loadInfo() {
        var name = binding.etName.text.toString()

        val homeController = ApiClient.getApiClient().create(HomeController::class.java)
        homeController.uploadMemberInfo(MemberInfoRequest(name, gender, createDate)).enqueue(object :
            Callback<ResponseTemplate<Void>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponseTemplate<Void>>,
                response: Response<ResponseTemplate<Void>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    Toast.makeText(this@SignUpActivity, "가입이 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Toast.makeText(this@SignUpActivity, "가입이 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<Void>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}