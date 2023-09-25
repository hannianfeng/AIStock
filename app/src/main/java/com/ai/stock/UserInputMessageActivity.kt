package com.ai.stock

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ai.mylibrary.BaseActivity
import com.ai.stock.databinding.ActivityUserInputMessageBinding

class UserInputMessageActivity : BaseActivity<ActivityUserInputMessageBinding>() {
    private lateinit var mEtUsername:EditText
    private lateinit var mImgNicknameClean:ImageView
    private lateinit var mBtnComplete:Button
    private lateinit var mTvContent1:TextView
    private lateinit var mTvContent2:TextView
    private lateinit var mTvContent3:TextView
    private lateinit var mTvSkip:TextView
    private lateinit var listTv:MutableList<TextView>
    private var isChoose=false
    override fun getView(): ActivityUserInputMessageBinding {
        return ActivityUserInputMessageBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
        initEvent()
    }
    private fun initView() {
        mEtUsername=vb.etNickname
        mImgNicknameClean=vb.imgNicknameClean
        mBtnComplete=vb.btnComplete
        mTvContent1=vb.tvContent1
        mTvContent2=vb.tvContent2
        mTvContent3=vb.tvContent3
        mTvSkip=vb.tvSkip
        listTv = mutableListOf(mTvContent1,mTvContent2,mTvContent3)
    }
    private fun initEvent() {
        //跳过
        mTvSkip.setOnClickListener {
            toPageFinish(BottomNavActivity::class.java)
        }
        //输入昵称
        mEtUsername.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()){
                    mImgNicknameClean.visibility= View.VISIBLE
                }else{
                    mImgNicknameClean.visibility=View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        mImgNicknameClean.setOnClickListener {
            mEtUsername.text.clear()
            mImgNicknameClean.visibility=View.INVISIBLE
        }
        mBtnComplete.setOnClickListener {
            val username = mEtUsername.text.toString()
            if (username.isEmpty()){
                toast("最初にニックネームを設定してください")
                log("not null")
                return@setOnClickListener
            }
            if (!isChoose){
                toast("興味のあるものを選択してください")
            }else{
                toPageFinish(BottomNavActivity::class.java)
            }
        }
        mTvContent1.setOnClickListener {
            setBgRes(mTvContent1)
        }
        mTvContent2.setOnClickListener {
            setBgRes(mTvContent2)
        }
        mTvContent3.setOnClickListener {
            setBgRes(mTvContent3)
        }
    }
    fun setBgRes(textView: TextView){
        for (i in listTv){
            if (i==textView){
                textView.setBackgroundResource(R.drawable.choose_content_border_ok)
                textView.setTextColor(Color.parseColor("#202020"))
                isChoose=true
            }else{
                i.setBackgroundResource(R.drawable.choose_content_border)
                i.setTextColor(Color.parseColor("#646566"))
            }
        }
    }
}