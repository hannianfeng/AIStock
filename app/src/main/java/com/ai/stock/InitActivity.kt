package com.ai.stock

import android.app.Application
import android.provider.SyncStateContract.Constants
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.gyf.immersionbar.ImmersionBar
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class InitActivity : Application() {
    override fun onCreate() {
        super.onCreate()
        // 创建WorkRequest
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // 可选：要求有网络连接
            .build()

//        val workRequest = PeriodicWorkRequest<MyWorkManager>(
//            1, // 设置任务的重复周期，单位是天
//            TimeUnit.DAYS
//        )
//            .setConstraints(constraints)
//            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS) // 设置初始延迟
//            .build()
        val workRequest = PeriodicWorkRequest
            .Builder(MyWorkManager::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(),TimeUnit.MILLISECONDS)
            .build()

        // 将WorkRequest加入WorkManager的队列
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "my_unique_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 24)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            // 如果当前时间已经超过12点，设置为明天的12点
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis - System.currentTimeMillis()
    }
}