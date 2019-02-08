package com.example.newbiechen.ireader.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.BaseTabActivity
import com.example.newbiechen.ireader.ui.dialog.SexChooseDialog
import com.example.newbiechen.ireader.ui.fragment.BookShelfFragment
import com.example.newbiechen.ireader.ui.fragment.CommunityFragment
import com.example.newbiechen.ireader.ui.fragment.FindFragment
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.PermissionsChecker
import com.example.newbiechen.ireader.utils.SharedPreUtils
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : BaseTabActivity() {

    companion object {
        private const val WAIT_INTERVAL = 2000L
        private const val PERMISSIONS_REQUEST_STORAGE = 1
        private val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val permissionsChecker = PermissionsChecker(this)
            //获取读取和写入SD卡的权限
            if (permissionsChecker.lacksPermissions(*PERMISSIONS)) {
                //请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_STORAGE)
            }


        }
    }


    override fun getContentId() = R.layout.activity_base_tab

    override fun createTabFragments(): MutableList<Fragment> {
        val fragments = mutableListOf<Fragment>()
        fragments.add(BookShelfFragment())
        fragments.add(CommunityFragment())
        fragments.add(FindFragment())
        return fragments

    }

    override fun createTabTitles() = resources.getStringArray(R.array.nb_fragment_title).toMutableList()

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        toolbar.setLogo(R.mipmap.logo)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = ""

    }

    override fun initWidget() {
        super.initWidget()
        showSexChooseDialog()
    }

    private fun showSexChooseDialog() {
        val sex = SharedPreUtils.getString(Constant.SHARED_SEX)
        if (sex.isNullOrEmpty()) {
            mVp.postDelayed({
                val dialog = SexChooseDialog(this)
                dialog.show()
            }, 500)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu?): Boolean {
        if (menu != null && menu is MenuBuilder) {
            try {
                val method = menu::class.java.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
                method.isAccessible = true
                method.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onPreparePanel(featureId, view, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> startActivity<SearchActivity>()
            R.id.action_download -> startActivity<DownloadActivity>()
            R.id.action_scan_local_book -> {
                startActivity<FileSystemActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_STORAGE -> {
                // 如果取消权限，则返回的值为0
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    toast("用户拒绝开启读写权限")
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private var isPrepareFinish = false

    override fun onBackPressed() {
        if (!isPrepareFinish) {
            mVp.postDelayed({ isPrepareFinish = false }, WAIT_INTERVAL)
        }
        isPrepareFinish = true
        toast("再按一次退出")
        super.onBackPressed()
    }



}