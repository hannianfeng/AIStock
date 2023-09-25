package com.ai.stock

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ai.mylibrary.AppUrl
import com.ai.mylibrary.BaseActivity
import com.ai.mylibrary.DeviceInfo
import com.ai.mylibrary.RetrofitUtil
import com.ai.mylibrary.bean.GetVerBean
import com.ai.mylibrary.bean.UserTokenBean
import com.ai.stock.databinding.ActivityLoginBinding
import com.ai.stock.dialog.LoginBottomSheetFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private lateinit var mEdTelephone: EditText
    private lateinit var mEdCaptche: EditText
    private lateinit var mLinTelephone: LinearLayout
    private lateinit var mImgClean: ImageView
    private lateinit var mImgAgree: ImageView
    private lateinit var mTvCaptcha: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var mBtnLogin: Button
    private var captche = true
    private var imgAgree = false
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var deviceInfo: DeviceInfo
    private lateinit var mIvTelephoneChoose: ImageView
    private var telephonePrefix = "080"
    private lateinit var tel: String
    override fun getView(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun init() {
        val permission = android.Manifest.permission.READ_PHONE_STATE

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        } else {
            // Permission is already granted, proceed with accessing device identifiers.
            deviceInfo = DeviceInfo(this)
        }

        initView()
        initEvent()
    }

    private fun initView() {
        mEdTelephone = vb.edTelephone
        mLinTelephone = vb.linTelephone
        mImgClean = vb.imgTelephoneClean
        mEdCaptche = vb.etCaptcha
        mTvCaptcha = vb.tvCaptcha
        mBtnLogin = vb.btnLogin

        mIvTelephoneChoose = vb.telephoneChoose
//        mBtnLogin.isEnabled = false
        mImgAgree = vb.imgAgree
        //获取焦点，软键盘
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mEdTelephone.requestFocus()
        inputMethodManager.showSoftInput(mEdTelephone, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun initEvent() {
        //输入电话号码
        mEdTelephone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    mLinTelephone.setBackgroundResource(R.drawable.telephone_border_bk)
                    mImgClean.visibility = View.VISIBLE
                    mTvCaptcha.setTextColor(Color.parseColor("#202020"))
                } else {
                    mLinTelephone.setBackgroundResource(R.drawable.telephone_border)
                    mImgClean.visibility = View.GONE
                    mTvCaptcha.setTextColor(Color.parseColor("#C8CACC"))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        //清除动画号码
        mImgClean.setOnClickListener {
            mEdTelephone.text.clear()
            mLinTelephone.setBackgroundResource(R.drawable.telephone_border)
            mImgClean.visibility = View.GONE
            mTvCaptcha.setTextColor(Color.parseColor("#C8CACC"))

        }

        // 创建一个35秒的倒计时器，每1秒触发一次
        countDownTimer = object : CountDownTimer(35000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // 每秒执行一次
                val secondsRemaining = millisUntilFinished / 1000
                // 在此处更新UI以显示剩余秒数
                // 例如，将秒数显示在TextView中
                mTvCaptcha.text = "$secondsRemaining 秒後に取得"
            }

            override fun onFinish() {
                // 倒计时完成时执行操作
                mTvCaptcha.text = "認証コードの取得"
                captche = true
            }
        }

        // 验证码启动倒计时器
        mTvCaptcha.setOnClickListener {
            tel = telephonePrefix + mEdTelephone.text.toString().trim()
            log(tel)
            if (tel.isNotEmpty()) {
                if (captche) {
                    countDownTimer.start()
                    captche = false
                    //获取焦点，软键盘
                    mEdTelephone.clearFocus()
                    mEdCaptche.requestFocus()
                    inputMethodManager.showSoftInput(mEdCaptche, InputMethodManager.SHOW_IMPLICIT)
                    //获取验证码
                    getVer(tel)
                }
            } else {
                toast("番号が正しく入力されていません")
            }
        }
//        输入验证码
        mEdCaptche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    mBtnLogin.setBackgroundResource(R.drawable.loginbtn_border_ok)
                    mBtnLogin.setTextColor(Color.parseColor("#FFFFFF"))
                    mBtnLogin.isEnabled = true
                } else {
                    mBtnLogin.setBackgroundResource(R.drawable.loginbtn_border)
                    mBtnLogin.setTextColor(Color.parseColor("#000000"))
                    mBtnLogin.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        mBtnLogin.setOnClickListener {
            //验证码
            val captche = mEdCaptche.text.toString()
            if (captche.isNotEmpty()) {
                if (captche.isNotEmpty()) {
                    if (imgAgree) {
                        mBtnLogin.isEnabled = false
                        tel = telephonePrefix + mEdTelephone.text.toString().trim()
                        login(tel, captche)
                    } else {
                        //弹窗
                        val bottomSheetFragment = LoginBottomSheetFragment()
                        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                        bottomSheetFragment.setOnClick(object :
                            LoginBottomSheetFragment.OnButtonListener {
                            override fun onClick() {
                                imgAgree = true
                                mImgAgree.setImageResource(R.drawable.icon_picture_selected_ok)
                                bottomSheetFragment.dismiss()
                            }
                        })
                    }
                }
            } else {
                sharedPreferencesInstance().saveString(
                    "telephone",
                    telephonePrefix + mEdTelephone.text.toString().trim()
                )

                Toast.makeText(this@LoginActivity, "電話番号または確認コードが正しくない", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mImgAgree.setOnClickListener {
            imgAgree = !imgAgree
            if (imgAgree) {
                mImgAgree.setImageResource(R.drawable.icon_picture_selected_ok)
            } else {
                mImgAgree.setImageResource(R.drawable.icon_picture_selected)
            }
        }
        val popupList = listOf("080", "090")
        val popupWindow = createPopupWindow(this, popupList)

        mIvTelephoneChoose.setOnClickListener {
            popupWindow.showAsDropDown(mIvTelephoneChoose)
        }
        vb.telephoneChooseLinear.setOnClickListener {
            popupWindow.showAsDropDown(mIvTelephoneChoose)
        }
        val listView = popupWindow.contentView.findViewById<ListView>(R.id.popupListView)
        listView.setOnItemClickListener { _, _, position, _ ->
            telephonePrefix = popupList[position]
            vb.telephonePrefix.text = "+$telephonePrefix"
            // 处理选项点击事件
            // 在这里执行选中项后的操作
            popupWindow.dismiss()
        }
    }

    private fun createPopupWindow(context: Context, items: List<String>): PopupWindow {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)
        val listView = popupView.findViewById<ListView>(R.id.popupListView)

        // 创建适配器将数据绑定到ListView
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        // 创建PopupWindow对象
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(ColorDrawable())

        // 设置PopupWindow可以获取焦点，以便在显示时处理返回键事件
        popupWindow.isFocusable = true
        return popupWindow
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun login(telephone: String, captche: String) {
        vb.lottio.visibility = View.VISIBLE
        val osVersion = deviceInfo.getOSVersion()
        val deviceModel = deviceInfo.getDeviceModel()
        val ipAddress = deviceInfo.getIPAddress()
        val userAgent = deviceInfo.getUserAgent()
        val deviceId = deviceInfo.getDeviceId()

        log("获取手机系统信息:$osVersion 手机型号:$deviceModel ip:$ipAddress 用户代理:$userAgent 用户设备号:$deviceId")

        val build = FormBody.Builder()
            .add("code", captche)
            .add("version", osVersion)
            .add("model", deviceModel)
            .add("ip", ipAddress.toString())
            .add("userAgent", userAgent.toString())
            .add("dev", deviceId)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
//                val encryptAES = encryptAES(build.toString(), key, iv)
//                val body = Base64.encodeToString(encryptAES.toByteArray(), Base64.DEFAULT)
//                log(body)
                val captchaPostService =
                    RetrofitUtil().getRetrofit().create(RetrofitUtil.LoginPostService::class.java)
                val responseBodyCall = captchaPostService.postData(AppUrl.login, build)!!.execute()

                withContext(Dispatchers.Main) {
                    log("${responseBodyCall.code()}")
                    if (responseBodyCall.code() == 422) {
                        toast("確認コードが正しくありません。再度取得してください")
                        mTvCaptcha.text = "認証コードの取得"
//                        this@LoginActivity.captche = true
                        mBtnLogin.isEnabled = true
                        vb.lottio.visibility = View.GONE
                        return@withContext
                    }
                    if (responseBodyCall.code() == 200) {
                        val responseUser = responseBodyCall.body()!!.string()
                        val userTokenBean = gson(responseUser, UserTokenBean::class.java)
                        if (userTokenBean.access_token.isNotEmpty()) {
                            log(userTokenBean.access_token)
                            sharedPreferencesInstance().saveString(
                                "token",
                                userTokenBean.access_token
                            )
                            toPageFinish(BottomNavActivity::class.java)
                        }
                        mBtnLogin.isEnabled = true
                    } else {
                        toastError()
                    }
                    vb.lottio.visibility = View.GONE
                }
            } catch (e: Exception) {
                lifecycleScope.launch(Dispatchers.Main) {
                    log("login error $e")
                    vb.lottio.visibility = View.GONE
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getVer(telephone: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val build = FormBody.Builder()
                    .add("phone", telephone)
                    .build()
//                val encryptAES = encryptAES(build.toString(), key, iv)
//                val body = Base64.encodeToString(encryptAES.toByteArray(), Base64.DEFAULT)
                log(build.toString())
//                val body = base64(build.())
//                log("---$body")
                val captchaPostService =
                    RetrofitUtil().getRetrofit().create(RetrofitUtil.CaptchaPostService::class.java)
                val ver = captchaPostService.postData(AppUrl.ver, build)!!.execute()
                withContext(Dispatchers.Main) {
                    log("ver : ${ver.code()}")
//                    log(ver.body()!!.string())
                    if (ver.code() == 422 || ver.code() == 400) {
                        toast("電話番号が間違っている")
                        log(ver.toString())
                        return@withContext
                    }
                    val verStr = ver.body()!!.string()
                    val gson = gson(verStr, GetVerBean::class.java)
                    if (gson.code == 200) {
                        toast("確認コードが正常に送信されました")
                        sharedPreferencesInstance().saveString(
                            "telephone",
                            mEdTelephone.text.toString().trim()
                        )
                    } else {
                        toastError()
                    }
                }
            } catch (e: Exception) {
                log(e.toString())
                Looper.prepare()
                toastError()
                Looper.loop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在Activity销毁时停止倒计时器，以避免内存泄漏
        countDownTimer.cancel()
    }
}