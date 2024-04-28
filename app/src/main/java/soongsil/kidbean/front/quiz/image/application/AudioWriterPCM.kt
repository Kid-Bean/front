package soongsil.kidbean.front.quiz.image.application

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioWriterPCM(private val context: Context, var sessionId: String) {
    private var filename: String? = null
    private var speechFile: FileOutputStream? = null

    init {
        open(sessionId)
    }

    private fun open(sessionId: String) {
        // 내부 저장소의 앱 전용 디렉토리를 사용
        val file = File(context.filesDir, "$sessionId.pcm")
        filename = file.absolutePath
        speechFile = try {
            FileOutputStream(file)
        } catch (e: IOException) {
            System.err.println("Can't open file : $filename")
            null
        }
    }

    fun close() {
        try {
            speechFile?.close()
        } catch (e: IOException) {
            System.err.println("Can't close file : $filename")
        }
    }

    fun write(data: ShortArray) {
        val buffer = ByteBuffer.allocate(data.size * 2).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            data.forEach { putShort(it) }
        }
        try {
            speechFile?.write(buffer.array())
        } catch (e: IOException) {
            System.err.println("Can't write file : $filename")
        }
    }
}
