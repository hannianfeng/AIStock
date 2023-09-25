package com.ai.stock

import android.os.Build
import android.text.TextUtils
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ai.mylibrary.BaseActivity
import com.ai.stock.databinding.ActivityBottomNavBinding
import com.ai.stock.fragment.AIStockFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomNavActivity : BaseActivity<ActivityBottomNavBinding>() {
    private lateinit var mFragmentContainer: FrameLayout
    private lateinit var mBottomNavigation:BottomNavigationView
    override fun getView(): ActivityBottomNavBinding {
        return ActivityBottomNavBinding.inflate(layoutInflater)
    }

    override fun init() {
        initView()
        initEvent()
    }

    private fun initEvent() {

    }
    private fun initView() {
        mFragmentContainer=vb.fragmentContainer

        // 使用FragmentManager添加Fragment到容器中
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, AIStockFragment()).commit() // R.id.fragmentContainer是容器的ID

//        mBottomNavigation = vb.bottomnavigation
//        val layoutParams = mBottomNavigation.layoutParams as ViewGroup.MarginLayoutParams
//        layoutParams.bottomMargin = getNavigationBarHeight() // 设置底部距离
//        mBottomNavigation.layoutParams = layoutParams
    }

    override fun onPause() {
        super.onPause()
        getNavigationBarHeight()
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyLongPress(keyCode, event)
    }
    private fun getNavigationBarHeight() :Int{
        val resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        val height = resources.getDimensionPixelSize(resourceId);
        val resourceId2 = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        val mHasNavigationBar = resources.getBoolean(resourceId2)

        log("$resourceId,$mHasNavigationBar")
        return height
    }


}