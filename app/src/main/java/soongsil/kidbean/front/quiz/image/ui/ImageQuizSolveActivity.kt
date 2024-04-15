package soongsil.kidbean.front.quiz.image.ui

import RetrofitImpl.retrofit
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.naver.speech.clientapi.SpeechRecognitionResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.BuildConfig
import soongsil.kidbean.front.MainActivity
import android.Manifest
import android.annotation.SuppressLint
import retrofit2.Retrofit
import soongsil.kidbean.front.R
import soongsil.kidbean.front.databinding.ActivityImageQuizSolveBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.quiz.MyQuizActivity
import soongsil.kidbean.front.quiz.image.application.AudioWriterPCM
import soongsil.kidbean.front.quiz.image.application.NaverRecognizer
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizSolveListRequest
import soongsil.kidbean.front.quiz.image.dto.request.ImageQuizSolveRequest
import soongsil.kidbean.front.quiz.image.dto.response.ImageQuizSolveResponse
import soongsil.kidbean.front.quiz.image.presentation.ImageQuizController
import java.io.Serializable
import java.lang.ref.WeakReference


class ImageQuizSolveActivity : AppCompatActivity() {

    private lateinit var binding : ActivityImageQuizSolveBinding

    private lateinit var s3Url : String
    private lateinit var answer : String
    private var quizId: Long = -1L
    private var quizCount: Long = 1L
    private var memberId:Long = 1L

    private val CLIENT_ID = BuildConfig.CLOVA_CLIENT_ID

    private var handler: RecognitionHandler? = null
    private var naverRecognizer: NaverRecognizer? = null

    private var txtResult: Button? = null
    private var btnStart: Button? = null
    //mResult에 실제 대답한 내용 있음
    private var mResult: String? = null

    private var writer: AudioWriterPCM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImageQuizSolveBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        txtResult = binding.txtResult
        btnStart = binding.btnStart

        handler = RecognitionHandler(this)
        naverRecognizer = NaverRecognizer(this, handler!!, CLIENT_ID)

        Log.d("naverRecognizer status", naverRecognizer!!.speechRecognizer!!.isRunning.toString())

        btnStart?.setOnClickListener {
            if (!naverRecognizer!!.speechRecognizer!!.isRunning) {
                // Start button is pushed when SpeechRecognizer's state is inactive.
                // Run SpeechRecongizer by calling recognize().
                mResult = ""
                txtResult!!.text = "Connecting..."
                Log.d("text info", txtResult!!.text.toString())
                binding.btnStart.setText(R.string.str_stop)
                naverRecognizer!!.recognize()
            } else {
                Log.d("ImageQuiz", "stop and wait Final Result")
                btnStart!!.isEnabled = false
                naverRecognizer!!.speechRecognizer!!.stop()
            }
        }

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동 - 진짜 나가겠냐고 물어보기
            //val intent = Intent(this, ImageQuizListActivity::class.java)
            startActivity(intent)
        }

        quizId = intent.getLongExtra("quizId", 1)

        loadInfo()
        bottomSetting()
    }


    private fun bottomSetting() {
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 문제 풀기 화면으로 변경하기!
        binding.btnQuiz.setOnClickListener {
            val intent = Intent(this, MyQuizActivity::class.java)
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

    private fun loadInfo() {
        val imageQuizController = retrofit.create(ImageQuizController::class.java)
        imageQuizController.getRandomImageQuizByMember(memberId).enqueue(object :
            Callback<ResponseTemplate<ImageQuizSolveResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseTemplate<ImageQuizSolveResponse>>,
                response: Response<ResponseTemplate<ImageQuizSolveResponse>>,
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("post", "onResponse 성공: " + response.body().toString())

                    val body = response.body()?.results

                    // API로 가져온 이미지 넣기
                    val imageView: ImageView = binding.imgQuiz
                    s3Url = body?.s3Url.toString()

                    Glide.with(this@ImageQuizSolveActivity)
                        .load(s3Url)
                        .into(imageView)

                    imageView.visibility = View.VISIBLE

                    // API로 가져온 정답 넣기
                    answer = body?.answer.toString()
                    quizId = body?.quizId!!
                    quizCount = intent.getLongExtra("quizCount", 1)
                    binding.tvCount.text = "$quizCount/5"

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<ImageQuizSolveResponse>>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("post", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    // Handle speech recognition Messages.
    private fun handleMessage(msg: Message) {
        when (msg.what) {
            R.id.clientReady -> {
                // Now an user can speak.
                txtResult!!.text = "Connected"
                writer = AudioWriterPCM(
                    Environment.getExternalStorageDirectory().absolutePath + "/NaverSpeechTest"
                )
                writer!!.open("Test")
            }

            R.id.audioRecording -> writer!!.write((msg.obj as ShortArray))
            R.id.partialResult -> {
                // Extract obj property typed with String.
                mResult = msg.obj as String
                txtResult!!.text = mResult
            }

            R.id.finalResult -> {
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                val speechRecognitionResult = msg.obj as SpeechRecognitionResult
                val result = speechRecognitionResult.results[0]
                val strBuf = StringBuilder()
                strBuf.append(result)
                mResult = strBuf.toString()
                txtResult!!.text = mResult

                showRecordingStoppedAlertDialog()
                txtResult?.setText(answer)
                txtResult?.visibility = View.VISIBLE
            }

            R.id.recognitionError -> {
                if (writer != null) {
                    writer!!.close()
                }
                mResult = "Error code : " + msg.obj.toString()
                txtResult!!.text = mResult
                binding.btnStart.setText(R.string.str_start)
                btnStart!!.isEnabled = true
            }

            R.id.clientInactive -> {
                if (writer != null) {
                    writer!!.close()
                }
                binding.btnStart.setText(R.string.str_start)
                btnStart!!.isEnabled = true
            }
        }
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    internal class RecognitionHandler(activity: ImageQuizSolveActivity) : Handler() {
        private val mActivity: WeakReference<ImageQuizSolveActivity>

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
        AlertDialog.Builder(this).apply {
            setTitle("문제 풀기 완료")
            setMessage("다음 문제로 넘어가시겠어요?\n입력한 정답 : " + mResult.toString())

            Log.d("answer", txtResult?.text.toString())

            setPositiveButton("다음 문제로") { _, _ ->
                binding.btnStart.setText(R.string.str_start)
                binding.txtResult.setText(answer)
                finish()

                Log.d("final answer", mResult.toString())

                // 이전에 풀었던 문제들
                // Intent에서 Serializable 데이터를 받아오고, 이를 안전하게 Map<Long, String>으로 캐스팅
                // 만약 null이면 빈 Map을 생성
                val originalMap = intent.getSerializableExtra("mapData") as? Map<Long, String> ?: emptyMap()

                //원본 Map을 MutableMap으로 변환 후 이번에 푼 문제 정보 추가 - 중복된 문제 없게 나중에 바꿔주기 or 무시
                val mutableMap = originalMap.toMutableMap()
                mutableMap[quizId] = mResult.toString()

                // mapData의 모든 키와 값을 로그로 출력
                mutableMap.forEach { (key, value) ->
                    Log.d("MapData", "Key: $key, Value: $value")
                }

                //5번째 문제 - 푼 문제들을 전부 제출
                if (quizCount == 5L) {
                    val quizSolveRequestList = mutableMap.map { (key, value) ->
                        ImageQuizSolveRequest(quizId = key, answer = value)
                    }

                    val request = ImageQuizSolveListRequest(quizSolvedRequestList = quizSolveRequestList)

                    val call = retrofit.create(ImageQuizController::class.java).solveImageQuiz(memberId, request)

                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("request", "success")
                            } else {
                                Log.d("request", "fail")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            // 네트워크 에러 등 요청 실패 처리
                            Log.d("request", "fail network")
                        }
                    })
                } else {    //다음 문제 풀기 시작

                    naverRecognizer!!.speechRecognizer!!.release()
                    Log.d("recognizer die", "true")

                    val intent = Intent(this@ImageQuizSolveActivity, ImageQuizSolveActivity::class.java)
                    // 현재 태스크의 모든 액티비티를 제거하고, 새로운 태스크를 생성하여 그 안에서 액티비티를 실행
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK

                    intent.putExtra("mapData", mutableMap as Serializable) // Map을 Serializable로 캐스팅
                    intent.putExtra("quizCount", quizCount + 1L)

                    startActivity(intent)
                    finish()
                }
            }
        }.create().show()
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

    override fun onStart() {
        super.onStart()
        // NOTE : initialize() must be called on start time.
        naverRecognizer!!.speechRecognizer!!.initialize()
        Log.d("recognizer start", "true")
    }

    override fun onResume() {
        super.onResume()
        mResult = ""
        txtResult!!.text = ""
        binding.btnStart.setText(R.string.str_start)
        btnStart!!.isEnabled = true
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 200 // 예시로 사용된 숫자
    }
}