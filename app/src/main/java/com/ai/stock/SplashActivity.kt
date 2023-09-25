package com.ai.stock

import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.lifecycleScope
import com.ai.mylibrary.BaseActivity
import com.ai.stock.databinding.ActivitySplashBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun getView(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun init(){
        val encryptAES = encryptAES("123", key, iv)
        log("==== $encryptAES")
        val decryptAES = decryptAES("iP/lJfH8l0Uh6LM4flxgjQ==", key, iv)
        log("----- $decryptAES")

        val logo = vb.logo
        // 加载图片并设置圆角
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(20)) // 设置圆角半径，可以根据需求调整
        Glide.with(this)
            .load(R.mipmap.logo) // 替换成你的图片资源
            .apply(requestOptions)
            .into(logo)

        val token = sharedPreferencesInstance().getString("token", "")
        log(token)
        val countDownTimer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // 在倒计时的每个间隔执行的操作
                // 在这里更新 UI 显示剩余秒数
                // 例如，更新 TextView 的文本
                // textView.text = "剩余时间: $secondsRemaining 秒"
            }

            override fun onFinish() {
                // 倒计时结束时执行的操作
                // 在这里可以执行需要在倒计时结束后触发的操作
                if (token.isEmpty()) {
                    toPageFinish(LoginActivity::class.java)
                } else {
                    toPageFinish(BottomNavActivity::class.java)
                }
            }
        }

        // 启动倒计时
        countDownTimer.start()
    }
}