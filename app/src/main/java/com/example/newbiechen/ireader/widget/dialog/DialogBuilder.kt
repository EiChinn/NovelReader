package com.example.newbiechen.ireader.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout

import androidx.appcompat.app.AlertDialog

/**
 * Created by chinnei on 2016/9/20.
 */

class DialogBuilder(builder: Builder) {
    private val context: Context?
    private val layoutId: Int

    private val layoutDialog: LayoutDialog?
    private val cancelable: Boolean
    private val canceledOnTouchOutside: Boolean
    private val darkWhenDialogShow: Boolean
    private val onCancelListener: DialogInterface.OnCancelListener?
    private val hasEdiText: Boolean
    private val dialogStyle: Int
    private val isDialogFillScreen: Boolean

    class Builder(// Required parameters
            val context: Context, val layoutId: Int) {

        // Optional parameters - initialized to default values
        var cancelable = false
        var canceledOnTouchOutside = false
        var darkWhenDialogShow = true
        var onCancelListener: DialogInterface.OnCancelListener? = null
        var hasEdiText = false
        var dialogStyle = -1
        var layoutDialog: LayoutDialog? = null
        var isDialogFillScreen: Boolean = false

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setCanceledOnTouchOutside(canceledOnTouchOutside: Boolean): Builder {
            this.canceledOnTouchOutside = canceledOnTouchOutside
            return this
        }

        fun setDarkWhenDialogShow(darkWhenDialogShow: Boolean): Builder {
            this.darkWhenDialogShow = darkWhenDialogShow
            return this
        }

        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?): Builder {
            this.onCancelListener = onCancelListener
            return this
        }

        fun setHasEdiText(hasEdiText: Boolean): Builder {
            this.hasEdiText = hasEdiText
            return this
        }

        fun setDialogStyle(dialogStyle: Int): Builder {
            this.dialogStyle = dialogStyle
            return this
        }

        fun setLayoutDialog(layoutDialog: LayoutDialog): Builder {
            this.layoutDialog = layoutDialog
            return this
        }

        fun setDialogFillScreen(dialogFillScreen: Boolean): Builder {
            isDialogFillScreen = dialogFillScreen
            return this
        }

        fun build(): Dialog {
            return DialogBuilder(this).createDialog()
        }


    }

    init {
        this.context = builder.context
        this.layoutId = builder.layoutId

        this.cancelable = builder.cancelable
        this.canceledOnTouchOutside = builder.canceledOnTouchOutside
        this.darkWhenDialogShow = builder.darkWhenDialogShow
        this.onCancelListener = builder.onCancelListener
        this.hasEdiText = builder.hasEdiText
        this.dialogStyle = builder.dialogStyle
        this.layoutDialog = builder.layoutDialog
        this.isDialogFillScreen = builder.isDialogFillScreen
    }

    fun createDialog(): Dialog {
        val dialog: Dialog = if (hasEdiText) {
            AlertDialog.Builder(context!!, dialogStyle).setView(EditText(context)).create()
        } else {
            AlertDialog.Builder(context!!, dialogStyle).create()
        }
        if (context is Activity && !context.isFinishing) {
            dialog.show()
        }
        dialog.setCancelable(cancelable)//设置是否可以取消这个对话框
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)//设置是否取消窗外的触动
        if (isDialogFillScreen) {
            dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        } else {
            dialog.window!!.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        }
        if (!darkWhenDialogShow) {
            dialog.window!!.setDimAmount(0f)
        }
        if (cancelable && null != onCancelListener) {
            dialog.setOnCancelListener(onCancelListener)
        }
        val viewLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        viewLp.gravity = Gravity.CENTER_HORIZONTAL
        val dialogViewHolder = DialogViewHolder.getDialogViewHolder(context, layoutId)
        layoutDialog?.convert(dialogViewHolder, dialog)
        dialog.window!!.setContentView(dialogViewHolder.convertView, viewLp)
        return dialog
    }

    interface LayoutDialog {
        fun convert(dialogViewHolder: DialogViewHolder, dialog: Dialog)
    }

    companion object {

        fun getBuilder(context: Context, layoutId: Int): Builder {
            return Builder(context, layoutId)
        }
    }
}
