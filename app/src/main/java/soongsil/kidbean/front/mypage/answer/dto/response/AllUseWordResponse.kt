package soongsil.kidbean.front.mypage.answer.dto.response

data class AllUseWordResponse(
    val wordInfoList: List<UseWordInfo>
) {

    data class UseWordInfo(
        val word: String,
        val count: Long
    ) {

    }

}
