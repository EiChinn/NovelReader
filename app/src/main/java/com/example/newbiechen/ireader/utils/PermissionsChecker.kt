package com.example.newbiechen.ireader.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionsChecker(context: Context) {
    private val mContext = context.applicationContext

    fun lacksPermissions(vararg permissions: String): Boolean {
        permissions.forEach {
            if (lacksPermission(it)) {
                return true
            }
        }
        return false
    }

    private fun lacksPermission(permission: String) = ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED
}

