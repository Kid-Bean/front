package soongsil.kidbean.front.quiz.image.dto.response

import com.google.gson.annotations.SerializedName

data class ImageQuizMemberDetailResponse(
    val title: String,
    @SerializedName(value = "imageUrl")
    val imgUrl: String,
    val answer: String,
    val category: String
) {
}