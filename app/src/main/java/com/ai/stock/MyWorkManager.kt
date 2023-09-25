package com.ai.stock

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ai.mylibrary.SharedPreferencesManager
import kotlin.math.log

class MyWorkManager(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        // 在这里执行数据修改操作
        // 这里的代码将在后台线程中执行
        SharedPreferencesManager.getInstance(context).saveBoolean("isLike1",false)
        SharedPreferencesManager.getInstance(context).saveBoolean("isLike2",false)
        SharedPreferencesManager.getInstance(context).saveBoolean("isLike3",false)
        SharedPreferencesManager.getInstance(context).saveBoolean("isLike4",false)
        val boolean = SharedPreferencesManager.getInstance(context).getBoolean("isLike1", false)
        Log.e("---TAG---","$boolean")
        return Result.success()
    }
}
