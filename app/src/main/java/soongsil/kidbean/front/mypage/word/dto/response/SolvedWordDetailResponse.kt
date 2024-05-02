package soongsil.kidbean.front.mypage.word.dto.response

data class SolvedWordDetailResponse(
    val solvedId : Long,
    val title : String,
    val wordList : List<String>,
    val answer : String,
    val kidAnswer : String
) {

}
