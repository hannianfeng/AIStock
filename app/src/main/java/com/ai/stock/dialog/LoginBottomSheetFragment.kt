package com.ai.stock.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ai.stock.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginBottomSheetFragment : BottomSheetDialogFragment() {
    private var onButtonListener: OnButtonListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_bottomdialogfragment, container, false)
        val mAgreeButton = view.findViewById<Button>(R.id.btn_bottom_dialog_agree)
        mAgreeButton.setOnClickListener {
            onButtonListener!!.onClick()
        }
        return view
    }
    fun setOnClick(onButtonListener: OnButtonListener?){
        this.onButtonListener=onButtonListener
    }

    interface OnButtonListener {
        fun onClick()
    }
}