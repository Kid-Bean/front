    package soongsil.kidbean.front.quiz.answer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naver.speech.clientapi.SpeechRecognitionResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.BuildConfig
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityAnswerQuizSolveBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveResponse
import soongsil.kidbean.front.quiz.answer.dto.response.AnswerQuizSolveScoreResponse
import soongsil.kidbean.front.quiz.answer.presentation.AnswerQuizController
import soongsil.kidbean.front.quiz.image.application.AudioWriterPCM
import soongsil.kidbean.front.quiz.image.application.NaverRecognizer
import soongsil.kidbean.front.util.ApiClient
import java.io.File
import java.lang.ref.WeakReference

    class AnswerQuizSolveActivity : AppCompatActivity() {

        private lateinit var binding : ActivityAnswerQuizSolveBinding
        private var quizId: Long = -1L
        private lateinit var title: String
        private var score:Long = -1L

        private val CLIENT_ID = BuildConfig.CLOVA_CLIENT_ID
        private var handler: RecognitionHandler? = null
        private var naverRecognizer: NaverRecognizer? = null
        private var btnStart: Button? = null
        private var mResult: String? = null
        private var writer: AudioWriterPCM? = null

        private var sessionId: String = "Test"


        override fun onCreate(savedInstanceState: Bundle?) {
            binding = ActivityAnswerQuizSolveBinding.inflate(layoutInflater)
            super.onCreate(savedInstanceState)
            setContentView(binding.root)

            ApiClient.init(this)

            // 오디오 녹음 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE
                )
            }

            btnStart = binding.btnStart

            handler = RecognitionHandler(this)
            naverRecognizer = NaverRecognizer(this, handler!!, CLIENT_ID)

            btnStart?.setOnClickListener {
                if (!naverRecognizer!!.speechRecognizer!!.isRunning) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = ""
                    binding.btnStart.setText(R.string.str_stop)
                    naverRecognizer!!.recognize()
                } else {
                    Log.d("ImageQuiz", "stop and wait Final Result")
                    btnStart!!.isEnabled = false
                    naverRecognizer!!.speechRecognizer!!.stop()
                }
            }

            loadInfo()
            bottomSetting()
        }

        private fun bottomSetting() {
            binding.btnHome.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            // 문제 풀기 화면으로 변경하기!
            binding.btnQuiz.setOnClickListener {
                val intent = Intent(this, QuizListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            // 프로그램 화면으로 변경하기!
            binding.btnProgram.setOnClickListener {
                /*val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)*/
            }

        // 마이페이지 화면으로 변경하기!
        binding.btnProgram.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

        private fun loadInfo() {
            val imageQuizController = ApiClient.getApiClient().create(AnswerQuizController::class.java)
            imageQuizController.getRandomAnswerQuizByMember().enqueue(object :
                Callback<ResponseTemplate<AnswerQuizSolveResponse>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseTemplate<AnswerQuizSolveResponse>>,
                    response: Response<ResponseTemplate<AnswerQuizSolveResponse>>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString())

                        val body = response.body()?.results

                        // API로 가져온 이미지 넣기
                        binding.tvQuiz.text = body?.question

                        // API로 가져온 제목
                        title = body?.title!!

                        // API로 가져온 정답 넣기
                        quizId = body.quizId

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패 + ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseTemplate<AnswerQuizSolveResponse>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
        }

        private fun handleMessage(msg: Message) {
            when (msg.what) {
                R.id.clientReady -> {
                    // Now an user can speak.
                    writer = AudioWriterPCM(this, sessionId)

                    Log.d("file path", "/NaverSpeechTest")
                    Log.d("file path", Environment.getExternalStorageDirectory().absolutePath + "/NaverSpeechTest")
                }

                R.id.audioRecording -> writer!!.write((msg.obj as ShortArray))
                R.id.partialResult -> {
                    // Extract obj property typed with String.
                    mResult = msg.obj as String
                }

                R.id.finalResult -> {
                    // Extract obj property typed with String array.
                    // The first element is recognition result for speech.
                    val speechRecognitionResult = msg.obj as SpeechRecognitionResult
                    val result = speechRecognitionResult.results[0]
                    val strBuf = StringBuilder()
                    strBuf.append(result)
                    mResult = strBuf.toString()

                    showRecordingStoppedAlertDialog()
                }

                R.id.recognitionError -> {
                    if (writer != null) {
                        writer!!.close()
                    }
                    mResult = "Error code : " + msg.obj.toString()
                    binding.btnStart.setText(R.string.str_start)
                    btnStart!!.isEnabled = true
                }

                R.id.clientInactive -> {
                    if (writer != null) {
                        writer!!.close()
                    }

                    btnStart?.isClickable = false
                    btnStart?.isFocusable = false
                }
            }
        }

        internal class RecognitionHandler(activity: AnswerQuizSolveActivity) : Handler() {
            private val mActivity: WeakReference<AnswerQuizSolveActivity>

            init {
                mActivity = WeakReference(activity)
            }

            override fun handleMessage(msg: Message) {
                val activity = mActivity.get()
                activity?.handleMessage(msg)
            }
        }

        // 녹음이 끝나면 AlertDialog 표시
        private fun showRecordingStoppedAlertDialog() {

            val file = File(this.filesDir, "$sessionId.pcm")
            val requestFile = file.asRequestBody("audio/pcm".toMediaTypeOrNull())
            val recordPart = MultipartBody.Part.createFormData("record", file.name, requestFile)

            val quizData = """
                {
                    "quizId": "$quizId",
                    "answer": "${mResult.toString()}"
                }
                """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val quizDataPart = MultipartBody.Part.createFormData("answerQuizSolvedRequest", "", quizData)

            val answerQuizController = ApiClient.getApiClient().create(AnswerQuizController::class.java)
            answerQuizController.solveAnswerQuiz(recordPart, quizDataPart).enqueue(object :
                Callback<ResponseTemplate<AnswerQuizSolveScoreResponse>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseTemplate<AnswerQuizSolveScoreResponse>>,
                    response: Response<ResponseTemplate<AnswerQuizSolveScoreResponse>>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        Log.d("post", "onResponse 성공: " + response.body().toString())

                        val body = response.body()?.results

                        // API로 가져온 정답 넣기
                        score = body?.score!!

                        Log.d("score", score.toString())

                        //점수를 가지고 Home 화면으로 이동
                        val intent = Intent(this@AnswerQuizSolveActivity, AnswerQuizNextDialog::class.java)

                        intent.putExtra("score", score)
                        intent.putExtra("result", mResult.toString())

                        startActivity(intent)
                        naverRecognizer!!.speechRecognizer!!.release()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("post", "onResponse 실패 + ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseTemplate<AnswerQuizSolveScoreResponse>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
        }

        // 사용자가 권한 요청에 대한 응답을 받을 때 호출되는 메서드
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 권한을 승인한 경우 다음 작업 수행
                    // 예: 오디오 녹음 시작
                } else {
                    // 사용자가 권한을 거부한 경우, 권한이 필요한 기능을 사용할 수 없음을 알림
                    Toast.makeText(this, "오디오 녹음 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDestroy() {
            naverRecognizer!!.speechRecognizer!!.release()
            super.onDestroy()
        }

        override fun onStart() {
            super.onStart()
            // NOTE : initialize() must be called on start time.
            naverRecognizer!!.speechRecognizer!!.initialize()
            Log.d("recognizer start", "true")
        }

        override fun onResume() {
            super.onResume()
            mResult = ""
            binding.btnStart.setText(R.string.str_start)
            btnStart!!.isEnabled = true
        }

        companion object {
            private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 200 // 예시로 사용된 숫자
        }
    }