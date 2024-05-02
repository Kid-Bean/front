package soongsil.kidbean.front.quiz.image.dto.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageQuizSolveResponse(
    val answer: String,
    val s3Url: String,
    val quizId: Long
) : Parcelable