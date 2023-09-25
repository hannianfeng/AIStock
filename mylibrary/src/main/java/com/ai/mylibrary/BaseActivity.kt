package com.ai.mylibrary

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    protected lateinit var vb: VB
    protected val key = "MLs&TYEQqEW@XQdP"
    protected val iv = "rMJ2rfy6k\$axT^Ua"

    //com.freetime.parttime.jobs     stockpioneerapps
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = getView()
        setContentView(vb.root)
        ImmersionBar.with(this)
//            //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
            .transparentNavigationBar()
//            ////状态栏字体是深色，不写默认为亮色
            .statusBarDarkFont(true)
            .fullScreen(false)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
            .init()
        init()
    }

    abstract fun getView(): VB
    abstract fun init()
    fun getToken(): String {
        return sharedPreferencesInstance().getString("token", "")
    }

    fun sharedPreferencesInstance(): SharedPreferencesManager {
        return SharedPreferencesManager.getInstance(this)
    }

    protected fun decode(data: String): String {
        return String(Base64.decode(Base64.decode(data, Base64.NO_WRAP), Base64.NO_WRAP))
    }

    fun <T> gson(str: String, classOfT: Class<T>): T {
        return Gson().fromJson(str, classOfT)
    }

    protected fun log(text: String) {
        Log.e("---TAG---", "log: $text")
    }

    fun base64(originalText: String): String {
        val encodedText1 = Base64.encodeToString(originalText.toByteArray(), Base64.DEFAULT)

        // 第二次Base64编码
        return Base64.encodeToString(encodedText1.toByteArray(), Base64.DEFAULT)
    }

    //AES加密
    fun encryptAES(input: String, key: String, iv: String): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val secretKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
            val ivParameterSpec = IvParameterSpec(iv.toByteArray(StandardCharsets.UTF_8))
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            val encryptedBytes = cipher.doFinal(input.toByteArray(StandardCharsets.UTF_8))
            // 使用Base64编码将加密后的字节数组转换为字符串
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun decryptAES(input: String, key: String, iv: String): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
            val ivParameterSpec = IvParameterSpec(iv.toByteArray(StandardCharsets.UTF_8))

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

            // 使用Base64解码输入字符串并解密

            return String(
                cipher.doFinal(Base64.decode(input, Base64.DEFAULT)),
                StandardCharsets.UTF_8
            )
        } catch (e: Exception) {
            e.printStackTrace()
            log(e.toString())
        }
        return ""
    }

    fun toPage(cls: Class<*>?) {
        startActivity(Intent(this, cls))
    }

    fun toastError() {
        toast(getString(R.string.network_error))
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun toPageFinish(cls: Class<*>?) {
        startActivity(Intent(this, cls))
        finish()
    }
}