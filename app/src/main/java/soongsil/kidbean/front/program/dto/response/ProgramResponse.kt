package soongsil.kidbean.front.program.dto.response

data class ProgramResponse(
    val programId : Long,
    val programCategory : String,
    val departmentName : String,
    val place : String,
    val departmentS3Url: String,
    val programTitle : String,
    val isStar : Boolean
)