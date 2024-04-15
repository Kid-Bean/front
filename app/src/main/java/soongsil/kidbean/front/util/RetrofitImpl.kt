import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import soongsil.kidbean.front.BuildConfig

object RetrofitImpl {
    private const val URL = BuildConfig.BASE_URL

    val retrofit = Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}