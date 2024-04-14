package soongsil.kidbean.front.quiz.image.application

import soongsil.kidbean.front.R
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.annotation.WorkerThread
import com.naver.speech.clientapi.SpeechConfig
import com.naver.speech.clientapi.SpeechConfig.EndPointDetectType
import com.naver.speech.clientapi.SpeechConfig.LanguageType
import com.naver.speech.clientapi.SpeechRecognitionException
import com.naver.speech.clientapi.SpeechRecognitionListener
import com.naver.speech.clientapi.SpeechRecognitionResult
import com.naver.speech.clientapi.SpeechRecognizer


internal class NaverRecognizer(
    context: Context?,
    private val mHandler: Handler,
    clientId: String?
) :
    SpeechRecognitionListener {
    var speechRecognizer: SpeechRecognizer? = null

    init {
        try {
            speechRecognizer = SpeechRecognizer(context, clientId)
        } catch (e: SpeechRecognitionException) {
            e.printStackTrace()
        }
        speechRecognizer!!.setSpeechRecognitionListener(this)
    }

    fun recognize() {
        try {
            speechRecognizer!!.recognize(
                SpeechConfig(
                    LanguageType.KOREAN,
                    EndPointDetectType.AUTO
                )
            )
        } catch (e: SpeechRecognitionException) {
            e.printStackTrace()
        }
    }

    @WorkerThread
    override fun onInactive() {
        Log.d(TAG, "Event occurred : Inactive")
        val msg: Message = Message.obtain(mHandler, R.id.clientInactive)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onReady() {
        Log.d(TAG, "Event occurred : Ready")
        val msg: Message = Message.obtain(mHandler, R.id.clientReady)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onRecord(speech: ShortArray) {
        Log.d(TAG, "Event occurred : Record")
        val msg = Message.obtain(mHandler, R.id.audioRecording, speech)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onPartialResult(result: String) {
        Log.d(TAG, "Partial Result!! ($result)")
        val msg = Message.obtain(mHandler, R.id.partialResult, result)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected")
    }

    @WorkerThread
    override fun onResult(result: SpeechRecognitionResult) {
        Log.d(TAG, "Final Result!! (" + result.results[0] + ")")
        val msg = Message.obtain(mHandler, R.id.finalResult, result)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onError(errorCode: Int) {
        Log.d(TAG, "Error!! (" + Integer.toString(errorCode) + ")")
        val msg = Message.obtain(mHandler, R.id.recognitionError, errorCode)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetectTypeSelected(epdType: EndPointDetectType) {
        Log.d(
            TAG,
            "EndPointDetectType is selected!! (" + Integer.toString(epdType.toInteger()) + ")"
        )
        val msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType)
        msg.sendToTarget()
    }

    companion object {
        private val TAG = NaverRecognizer::class.java.simpleName
    }
}