package com.example.edugo

import android.app.Application

class GlobalApplication : Application(){
    override fun onCreate() {
        super.onCreate()

//        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}