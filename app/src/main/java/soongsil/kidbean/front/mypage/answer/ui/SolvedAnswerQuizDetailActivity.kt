package soongsil.kidbean.front.mypage.answer.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soongsil.kidbean.front.databinding.ActivityAnswerQuizSolvedResultBinding
import soongsil.kidbean.front.global.ResponseTemplate
import soongsil.kidbean.front.home.ui.MainActivity
import soongsil.kidbean.front.mypage.MypageActivity
import soongsil.kidbean.front.mypage.answer.dto.response.SolvedAnswerQuizDetailResponse
import soongsil.kidbean.front.mypage.presentation.MypageController
import soongsil.kidbean.front.quiz.QuizListActivity
import soongsil.kidbean.front.util.ApiClient
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Paths

class SolvedAnswerQuizDetailActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAnswerQuizSolvedResultBinding
    private var solvedId: Long = -1L
    private var mediaPlayer = MediaPlayer()

    private lateinit var pcmFile: File
    private lateinit var wavFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerQuizSolvedResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (applicationContext != null) {
            pcmFile = File(cacheDir, "audio.pcm")
            wavFile = File(cacheDir, "audio.wav")
        }


        ApiClient.init(this)

        binding.btnBack.setOnClickListener {
            // 홈 화면으로 이동
            val intent = Intent(this, SolvedAnswerQuizListActivity::class.java)
            startActivity(intent)
        }

        solvedId = intent.getLongExtra("solvedId", -1L)

        bottomSetting()

        bindData(solvedId)
    }

    private fun bindData(solvedId : Long) {
        val myPageController =
            ApiClient.getApiClient().create(MypageController::class.java)
        myPageController.findSolvedVoiceDetail(solvedId).enqueue(object :
            Callback<ResponseTemplate<SolvedAnswerQuizDetailResponse>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponseTemplate<SolvedAnswerQuizDetailResponse>>,
                response: Response<ResponseTemplate<SolvedAnswerQuizDetailResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString())
                    val body = response.body()?.results
                        ?: throw IllegalStateException("Response body is null")

                    bindQuizInfo(body)

                    setAdapter(body.checkList.checkList)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("post", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseTemplate<SolvedAnswerQuizDetailResponse>>, t: Throwable) {
                Log.d("post", "onResponse 실패 + ${t.message}")
            }
        })
    }

    private fun setAdapter(
        checkList: List<SolvedAnswerQuizDetailResponse.MorphemeCheckListResponse.MorphemeCheckListInfo>,
    ) {
        val sortCheckList = sortCheckList(checkList)

        val listAdapter = SolvedAnswerQuizCheckListAdapter(sortCheckList)
        val linearLayoutManager = LinearLayoutManager(this)

        binding.rvCheckList.adapter = listAdapter
        binding.rvCheckList.layoutManager = linearLayoutManager
        binding.rvCheckList.setHasFixedSize(true)
    }

    private fun sortCheckList(checkList: List<SolvedAnswerQuizDetailResponse.MorphemeCheckListResponse.MorphemeCheckListInfo>): List<SolvedAnswerQuizDetailResponse.MorphemeCheckListResponse.MorphemeCheckListInfo> {
        return checkList.sortedBy { item ->
            when (item.ageGroup) {
                "BEFORE_ONE" -> 0
                "ONE" -> 1
                "TWO" -> 2
                "THREE" -> 3
                "FOUR" -> 4
                "AFTER_FIVE" -> 5
                else -> throw IllegalArgumentException("Unknown age group: ${item.ageGroup}")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindQuizInfo(body: SolvedAnswerQuizDetailResponse) {
        binding.tvQuestion.text = body.quizContent
        binding.tvKidAnswer.text = body.kidAnswer

        downloadWavFromPcmUrl(body.answerUrl)
        mediaPlayer.setDataSource(this, Uri.parse(body.answerUrl))

        binding.imKidAnswerSpeaker.setOnClickListener {
            mediaPlayer.start()
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
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pcmToWav(pcmFile: File, wavFile: File, sampleRate: Int, channels: Int, bitsPerSample: Int) {
        val pcmBytes = Files.readAllBytes(Paths.get(pcmFile.toURI()))

        val wavBytes = makeWavFormat(pcmBytes, 1)

        File(wavFile.toURI()).writeBytes(wavBytes as ByteArray)
    }

    private fun makeWavFormat(pcmData: ByteArray, channels: Int): Any {
        val sampleRate = 16000
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val wavHeader = ByteBuffer.allocate(44)
        wavHeader.order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        wavHeader.put("RIFF".toByteArray(Charsets.US_ASCII))
        wavHeader.putInt(36 + pcmData.size)
        wavHeader.put("WAVE".toByteArray(Charsets.US_ASCII))

        // fmt subchunk
        wavHeader.put("fmt ".toByteArray(Charsets.US_ASCII))
        wavHeader.putInt(16) // Subchunk1Size for PCM
        wavHeader.putShort(1) // AudioFormat (1 for PCM)
        wavHeader.putShort(channels.toShort())
        wavHeader.putInt(sampleRate)
        wavHeader.putInt(byteRate)
        wavHeader.putShort(blockAlign.toShort())
        wavHeader.putShort(bitsPerSample.toShort())

        // data subchunk
        wavHeader.put("data".toByteArray(Charsets.US_ASCII))
        wavHeader.putInt(pcmData.size)

        // Combine header and PCM data
        return ByteBuffer.allocate(wavHeader.capacity() + pcmData.size)
            .put(wavHeader.array())
            .put(pcmData)
            .array()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun downloadWavFromPcmUrl(pcmUrl: String) {
        // PCM 파일 다운로드
        val downloadThread = Thread {
            try {
                val url = URL(pcmUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(pcmFile)
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()
                connection.disconnect()

                // PCM 파일을 WAV 파일로 변환
                pcmToWav(pcmFile, wavFile, 44100, 1, 16)

                Log.d("media", pcmFile.absolutePath +  "  " + pcmFile.canRead().toString())
                Log.d("media", wavFile.absolutePath +  "  " + wavFile.canRead().toString())

                mediaPlayer.apply {
                    // 기존 MediaPlayer 리소스 해제
                    release()

                    // 새로운 MediaPlayer 생성
                    mediaPlayer = MediaPlayer().apply {
                        try {
                            setDataSource(wavFile.absolutePath)
                            prepare()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        downloadThread.start()
    }
}
