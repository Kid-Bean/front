package soongsil.kidbean.front.mypage.image.dto.response

data class MyPageImageScoreResponse(
    val myScoreInfo: List<MyScoreInfo>,
    val ageScoreInfo: List<AgeScoreInfo>
)

data class MyScoreInfo(
    val quizCategory: String,
    val score: Long,
    val quizCount: Long
)

data class AgeScoreInfo(
    val quizCategory: String,
    val score: Long,
    val memberCount: Long
)

