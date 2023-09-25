package com.ai.mylibrary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.net.Inet4Address
import java.net.NetworkInterface

class DeviceInfo(private val context: Context) {
    // 获取手机系统信息
    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    // 获取手机型号
    fun getDeviceModel(): String {
        return Build.MODEL
    }

    // 获取IP地址
    fun getIPAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    // 获取用户代理
    fun getUserAgent(): String? {
        return System.getProperty("http.agent")
    }

    // 获取用户设备号
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
    }
}