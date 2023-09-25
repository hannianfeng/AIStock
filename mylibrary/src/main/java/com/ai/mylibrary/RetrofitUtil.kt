package com.ai.mylibrary

import com.ai.mylibrary.bean.LikeIndexBean
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

class RetrofitUtil {
    interface ApiPostService {
        @POST("{path}")
        fun postData(
            @Path("path") path: String,
            @Body requestBody: FormBody?,
            @Header("token") token: String?
        ): Call<ResponseBody?>?
    }

    //login
    interface CaptchaPostService {
        @POST("{path}")
        fun postData(
            @Path("path") path: String,
            @Body requestBody: FormBody?,
        ): Call<ResponseBody?>?
    }
    interface LoginPostService {
        @POST("{path}")
        fun postData(
            @Path("path") path: String,
            @Body requestBody: FormBody?,
        ): Call<ResponseBody?>?
    }

    //点赞列表
    interface ApiLikeIndexListGetService {
        @GET("{path}")
        fun getData(
            @Path("path") path: String,
            @Header("token") token: String?
        ): Call<LikeIndexBean>?
    }

    interface ApiStockCountsGetService {
        @GET("{path}")
        fun getData(
            @Path("path") path: String?,
            @Header("token") token: String?
        ): Call<ResponseBody?>?
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // 设置连接超时时间
            .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间
            .writeTimeout(60, TimeUnit.SECONDS) // 设置写入超时时间
            .addInterceptor(OkHttpInterceptor())
            .build()
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppUrl.API) // 设置基本的 API 地址
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create()) // 添加 Gson 转换器，用于解析 JSON 数据
            .build()
    }

    private fun getRequest(path: String?, token: String?): Call<ResponseBody?>? {
        val service = getRetrofit().create(ApiStockCountsGetService::class.java)
        return service.getData(path, token)
    }

    fun getCall(path: String?, token: String?): Response<ResponseBody?> {
        getRequest(path, token)!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        return getRequest(path, token)!!.execute()
    }
}
