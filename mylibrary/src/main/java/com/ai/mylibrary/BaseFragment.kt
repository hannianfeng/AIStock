package com.ai.mylibrary

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentViewHolder
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

abstract class BaseFragment<VB : ViewBinding>: Fragment() {
    protected lateinit var vb: VB
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        vb=getViewBinding()
        ImmersionBar.with(this)
            //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
            .transparentNavigationBar()
            ////状态栏字体是深色，不写默认为亮色
//            .statusBarDarkFont(true)
            .fullScreen(false)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
            .init()
        init()
        return vb.root
    }
    abstract fun getViewBinding(): VB
    abstract fun init()
    fun getToken(): String {
        return sharedPreferencesInstance().getString("token", "")
    }
    fun sharedPreferencesInstance(): SharedPreferencesManager {
        return SharedPreferencesManager.getInstance(requireContext())
    }
    fun <T> gson(str: String, classOfT: Class<T>): T {
        return Gson().fromJson(str, classOfT)
    }
    //AES加密
    fun encryptAES(input: String, key: String, iv: String): String {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
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
    fun toPageFinish(cls:Class<*>?){
        context?.startActivity(Intent(context,cls))
        activity?.finish()
    }
    fun toPage(cls:Class<*>?){
        context?.startActivity(Intent(context,cls))
    }
    fun toast(text: String){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
    }
    fun toastError() {
        toast(getString(R.string.network_error))
    }
    fun log(text:String){
        Log.e("---TAG---", text )
    }
}