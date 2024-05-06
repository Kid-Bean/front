package soongsil.kidbean.front.quiz.word.dto.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WordQuizSolveResponse(
    val answer: String,
    val quizId: Long,
    val title: String,
    val words: List<Words>

) : Parcelable

@Parcelize
data class Words(
    val content: String
) : Parcelable