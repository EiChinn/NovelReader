package com.example.newbiechen.ireader.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.RecommendBookEvent
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.SharedPreUtils
import kotlinx.android.synthetic.main.dialog_sex_choose.*

class SexChooseDialog(context: Context) : Dialog(context, R.style.CommonDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sex_choose)
        setUpWindow()
        initClick()

    }

    private fun setUpWindow() {
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.horizontalMargin = 0f
    }

    private fun initClick() {
        choose_iv_close.setOnClickListener {
            dismiss()
        }
        choose_btn_boy.setOnClickListener {
            //保存到SharePreference中
            SharedPreUtils.putString(Constant.SHARED_SEX, Constant.SEX_BOY)
            RxBus.getInstance().post(RecommendBookEvent(Constant.SEX_BOY))
            dismiss()

        }
        choose_btn_boy.setOnClickListener {
            //保存到SharePreference中
            SharedPreUtils.putString(Constant.SHARED_SEX, Constant.SEX_BOY)
            RxBus.getInstance().post(RecommendBookEvent(Constant.SEX_GIRL))
            dismiss()

        }
    }
}