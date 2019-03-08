package com.example.newbiechen.ireader.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

import com.example.newbiechen.ireader.R


/**
 * 对话框帮助类
 * Created by zhengwenjie on 2015/12/1.
 */
object LoadingDialogHelper {

    private var loadingDialog: Dialog? = null
    private var isShowTv: Boolean = false

    fun isShowTv(isShowTv: Boolean) {
        LoadingDialogHelper.isShowTv = isShowTv
    }

    fun showLoadingDialog(context: Context, tipMsg: String) {
        showLoadingDialog(context, false, null)
    }

    /**
     *
     * @param context
     * @param cancelable
     * @param onCancelListener
     */
    @JvmOverloads
    fun showLoadingDialog(context: Context, cancelable: Boolean = false, onCancelListener: DialogInterface.OnCancelListener? = null) {
        closeLoadingDialog()
        loadingDialog = DialogBuilder.Builder(context, R.layout.progress_dialog)
                .setDialogStyle(R.style.LoadingDialog)
                .setCancelable(cancelable)
                .setDarkWhenDialogShow(false)
                .setOnCancelListener(onCancelListener)
                .build()
        //        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    fun closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
    }

}
/**
 * 显示通讯框 默认不可点击返回键取消
 * @param context
 */
/**
 *
 * @param context
 * @param cancelable
 */

