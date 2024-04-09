package soongsil.kidbean.front.quiz.image.dto.response

import com.google.gson.annotations.SerializedName

data class ImageQuizMemberDetailResponse(
    val title: String,
    val s3Url: String,
    val answer: String,
    val quizCategory: String
) {
}