import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitImpl {
    private const val URL = "https://10.0.2.2:8080"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(150, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()

    val retrofit = Builder()
        .baseUrl(URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}