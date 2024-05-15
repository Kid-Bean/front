package soongsil.kidbean.front.program.dto.response

data class ProgramDetailResponse(
    val programId: Long,
    val programTitle: String,
    val contentTitle: String,
    val content: String,
    val departmentName: String,
    val place: String,
    val phoneNumber: String,
    val programS3Url: String,
    val departmentS3Url: String,
    val date: List<String>
)
