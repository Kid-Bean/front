package soongsil.kidbean.front.quiz.image.dto.request

import com.google.gson.annotations.SerializedName

data class ImageQuizUpdateRequest(
    val title: String,
    val answer: String,
    val quizCategory: String
) {
}