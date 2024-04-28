package soongsil.kidbean.front.global

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import soongsil.kidbean.front.BuildConfig

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}