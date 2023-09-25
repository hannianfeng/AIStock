package com.ai.stock.fragment

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ai.mylibrary.AppUrl
import com.ai.mylibrary.BaseFragment
import com.ai.mylibrary.RetrofitUtil
import com.ai.mylibrary.bean.LikeIndexBean
import com.ai.mylibrary.bean.SaveStockInfoBean
import com.ai.stock.R
import com.ai.mylibrary.bean.StockCountBean
import com.ai.mylibrary.bean.Test
import com.ai.mylibrary.bean.UserAddLikeCountsBean
import com.ai.stock.LoginActivity
import com.ai.stock.databinding.DanmuLayoutBinding
import com.ai.stock.databinding.FragmentAIStockBinding
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jessyan.autosize.internal.CustomAdapt
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class AIStockFragment : BaseFragment<FragmentAIStockBinding>(), CustomAdapt {
    private lateinit var mBtnComplete: Button
    private val listDanmu =
        listOf("幸いなことに、AI がそれをテストし、時間内に...", "非常に正確", "プロフェッショナル", "やはりAIロボットが頼りです")
    private lateinit var danMu1: DanmuLayoutBinding
    private lateinit var danMu2: DanmuLayoutBinding
    private lateinit var danMu3: DanmuLayoutBinding
    private lateinit var danMu4: DanmuLayoutBinding
    private lateinit var mStockCount: TextView
    private lateinit var mIvLike1: ImageView
    private lateinit var mIvLike2: ImageView
    private lateinit var mIvLike3: ImageView
    private lateinit var mIvLike4: ImageView

    private lateinit var mTvLikeNum1: TextView
    private lateinit var mTvLikeNum2: TextView
    private lateinit var mTvLikeNum3: TextView
    private lateinit var mTvLikeNum4: TextView
    //点赞
    private lateinit var listLikeView: MutableList<TextView>
    private lateinit var listLikeImageView: MutableList<ImageView>
    private lateinit var likeIndex: LikeIndexBean
    private lateinit var mEtStockCode: EditText
    override fun getViewBinding(): FragmentAIStockBinding {
        return FragmentAIStockBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
        initData()
        initEvent()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initData() {
        //获取诊股计数
        getCounts()
        //获取点赞列表
        getLikeIndex()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getLikeIndex() {
        GlobalScope.launch(Dispatchers.IO) {
            val likeIndexService = RetrofitUtil().getRetrofit()
                .create(RetrofitUtil.ApiLikeIndexListGetService::class.java)
            val data = likeIndexService.getData(AppUrl.likeList,getToken())?.execute()
            withContext(Dispatchers.Main) {
                if (data!!.code() == 200) {
                    likeIndex = data.body()!!
                    log(likeIndex.toString())
                    for (i in 0..3) {
                        listLikeView[i].text = likeIndex?.get(i)?.likes.toString()
                        if (likeIndex[i].is_like){
                            listLikeView[i].setTextColor(Color.parseColor("#000000"))
                            listLikeImageView[i].isEnabled=false
                            listLikeImageView[i].setImageResource(R.drawable.like_black)
                        }

                    }
                } else {
                    toast(getString(R.string.network_error))
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getCounts() {
        GlobalScope.launch(Dispatchers.IO) {
            val countsGetService = RetrofitUtil().getRetrofit()
                .create(RetrofitUtil.ApiStockCountsGetService::class.java)
            val data = countsGetService.getData(AppUrl.stockCounts, null)?.execute()
            withContext(Dispatchers.Main) {
                if (data!!.code() == 200) {
                    val stockCount = data.body()!!.string()
                    val stockCountBean =
                        Gson().fromJson(stockCount.toString(), StockCountBean::class.java)
                    mStockCount.text = "累計${stockCountBean.data.count}名が株式診断レポートを受け取りました"
                } else {
                    toast(getString(R.string.network_error))
                }
            }
        }
    }

    private fun initEvent() {
        mBtnComplete.setOnClickListener {
            val stockCode = mEtStockCode.text.toString()
            if (stockCode.isEmpty()) {
                toast("ティッカーシンボルを入力してください")
                return@setOnClickListener
            }
            //保存号码 弹窗
            val telephone: String = sharedPreferencesInstance().getString("telephone", "")

            val customDialog = Dialog(requireContext())
            customDialog.setContentView(R.layout.fragment_dialog_code) // 设置自定义布局文件
            customDialog.window?.setBackgroundDrawableResource(R.drawable.fragment_dialog_border)
            customDialog.setCanceledOnTouchOutside(false)
            val mEtDialogTelephone = customDialog.findViewById<EditText>(R.id.dialog_telephone)
            if (telephone.isNotEmpty()) {
                mEtDialogTelephone.setText(telephone)
            }
            val mTvDialogStockCode = customDialog.findViewById<TextView>(R.id.stock_code)
            mTvDialogStockCode.text = "問い合わせている株は: $stockCode"
            //弹窗提交按钮
            val mBtnDialogComplete = customDialog.findViewById<Button>(R.id.btn_complete)
            mBtnDialogComplete.setOnClickListener {
                val telephone = mEtDialogTelephone.text.toString()
                if (telephone.isNotEmpty()) {
                    //保存信息
                    saveStockInfo(stockCode, telephone)
                    customDialog.dismiss()
                } else {
                    toast("携帯電話番号を入力してください")
                }
            }
            //取消弹窗
            val mImgDialogClose = customDialog.findViewById<ImageView>(R.id.dialog_close)
            mImgDialogClose.setOnClickListener {
                customDialog.dismiss() // 关闭弹窗
            }
            customDialog.show() // 显示弹窗

        }
        //弹幕点赞
        mIvLike1.setOnClickListener {
//            val isLike = sharedPreferencesInstance().getBoolean("isLike", false)
//            if (isLike){
//                toast(getString(R.string.like))
//                return@setOnClickListener
//            }
            mIvLike1.setImageResource(R.drawable.like_black)
            mTvLikeNum1.setTextColor(Color.parseColor("#000000"))
//            sharedPreferencesInstance().saveBoolean("isLike", true)
            userLike(mTvLikeNum1, 1)
        }
        mIvLike2.setOnClickListener {
//            val isLike = sharedPreferencesInstance().getBoolean("isLike", false)
//            if (isLike){
//                toast(getString(R.string.like))
//                return@setOnClickListener
//            }
            mIvLike2.setImageResource(R.drawable.like_black)
            mTvLikeNum2.setTextColor(Color.parseColor("#000000"))
//            sharedPreferencesInstance().saveBoolean("isLike", true)
            userLike(mTvLikeNum2, 2)
        }
        mIvLike3.setOnClickListener {
//            val isLike = sharedPreferencesInstance().getBoolean("isLike", false)
//            if (isLike){
//                return@setOnClickListener
//            }
            mIvLike3.setImageResource(R.drawable.like_black)
            mTvLikeNum3.setTextColor(Color.parseColor("#000000"))
//            sharedPreferencesInstance().saveBoolean("isLike", true)
            userLike(mTvLikeNum3, 3)
        }
        mIvLike4.setOnClickListener {
            val isLike = sharedPreferencesInstance().getBoolean("isLike", false)
            if (isLike){
                toast(getString(R.string.like))
                return@setOnClickListener
            }
            mIvLike4.setImageResource(R.drawable.like_black)
            mTvLikeNum4.setTextColor(Color.parseColor("#000000"))
//            sharedPreferencesInstance().saveBoolean("isLike", true)
            userLike(mTvLikeNum4, 4)
        }
    }

    //用户点赞
    @OptIn(DelicateCoroutinesApi::class)
    private fun userLike(likeText: TextView, likeId: Int) {
        var likeCount = likeText.text.toString().toInt()
        likeCount+=1
        likeText.text=likeCount.toString()
        val build = FormBody.Builder()
            .add("like_id", likeId.toString())
            .build()
        log(getToken())
//        log(build.name(0))
        GlobalScope.launch(Dispatchers.IO) {
            val service =
                RetrofitUtil().getRetrofit().create(RetrofitUtil.ApiPostService::class.java)
            val bodyCall = service.postData(AppUrl.like, build, getToken())!!.execute()
            withContext(Dispatchers.Main) {
                log(bodyCall.toString())
                log(bodyCall.code().toString())
                if (bodyCall.code() == 403) {
                    toPageFinish(LoginActivity::class.java)
                    return@withContext
                }
                if (bodyCall.code() == 429 || bodyCall.code() == 404) {
                    toast("あなたは今日すでにそれを気に入っています、明日戻ってきてください")
                    return@withContext
                }
                if (bodyCall.code() == 200) {
                    val addLike = bodyCall.body()!!.string()
                    log(addLike.toString())
                    val addLikeCountsBean = gson(addLike, UserAddLikeCountsBean::class.java)
                    log(addLikeCountsBean.toString())
                    likeText.text = addLikeCountsBean.data.likes.toString()
                } else {
                    toastError()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveStockInfo(stockCode: String, telephone: String) {
        val build = FormBody.Builder()
            .add("stock_code", stockCode)
            .add("stock_phone", telephone)
            .build()
//        val key = "MLs&TYEQqEW@XQdP"
//        val iv = "rMJ2rfy6k\$axT^Ua"
//        log(build.toString())
//        val encryptAES = encryptAES(build.toString(), key, iv)
        GlobalScope.launch(Dispatchers.IO) {
            val apiPostService =
                RetrofitUtil().getRetrofit().create(RetrofitUtil.ApiPostService::class.java)
            val saveInfo =
                apiPostService.postData(AppUrl.saveStockAndTelephone, build, getToken())?.execute()
            withContext(Dispatchers.Main) {
                log("服务器错误: ${saveInfo!!.code()}")
                if (saveInfo.code() == 500) {
                    toast("サーバーエラー")
                    return@withContext
                }
                if (saveInfo.code() == 403) {
                    toPageFinish(LoginActivity::class.java)
                    return@withContext
                }
                if (saveInfo.code() == 422 || saveInfo.code() == 400) {
                    toast("電話番号が間違っている")
                    return@withContext
                }
                if (saveInfo.code() == 200) {
                    val saveStockInfoBean =
                        gson(saveInfo.body()!!.string(), SaveStockInfoBean::class.java)
                    mStockCount.text = "累計${saveStockInfoBean.data[0].count}名が株式診断レポートを受け取りました"
                    //自定义Toast
                    val inflater = layoutInflater
                    val layout = inflater.inflate(R.layout.dialog_toast, null)
                    val toast = Toast(context)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.setGravity(Gravity.CENTER, 0, -50)
                    toast.view = layout
                    toast.show()
                } else {
                    toastError()
                }
            }
        }
    }

    private fun initView() {
        mBtnComplete = vb.btnComplete
        mEtStockCode = vb.userStockCode

        danMu1 = vb.danmu1
        mIvLike1 = danMu1.ivLike
        val mTvLike1 = danMu1.tvLike
        mTvLikeNum1 = danMu1.tvLikeNum
        mTvLike1.text = listDanmu[0]
        danMu2 = vb.danmu2
        mIvLike2 = danMu2.ivLike
        val mTvLike2 = danMu2.tvLike
        mTvLikeNum2 = danMu2.tvLikeNum
        mTvLike2.text = listDanmu[1]
        danMu3 = vb.danmu3
        mIvLike3 = danMu3.ivLike
        val mTvLike3 = danMu3.tvLike
        mTvLikeNum3 = danMu3.tvLikeNum
        mTvLike3.text = listDanmu[2]
        danMu4 = vb.danmu4
        mIvLike4 = danMu4.ivLike
        val mTvLike4 = danMu4.tvLike
        mTvLikeNum4 = danMu4.tvLikeNum
        mTvLike4.text = listDanmu[3]
        mStockCount = vb.stockCount
        listLikeView = mutableListOf(mTvLikeNum1, mTvLikeNum2, mTvLikeNum3, mTvLikeNum4)
        listLikeImageView= mutableListOf(mIvLike1,mIvLike2,mIvLike3,mIvLike4)
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun getSizeInDp(): Float {
        return 740F
    }
}