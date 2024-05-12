package soongsil.kidbean.front.program.dto.response

data class ProgramResponse(
    val programId : Long,
    val programCategory : String,
    val teacherName : String,
    val place : String
)