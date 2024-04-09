package soongsil.kidbean.front.global

import com.google.gson.annotations.SerializedName

data class ResponseTemplate<T>(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("code") var code: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("results") var results: T? = null,
)