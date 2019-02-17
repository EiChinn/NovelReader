package com.example.newbiechen.ireader.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobBatch
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BatchResult
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListListener
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.SyncBookEvent
import com.example.newbiechen.ireader.model.bean.BmobDaoUtils
import com.example.newbiechen.ireader.model.bean.CollBookBmobBean
import com.example.newbiechen.ireader.model.local.BookRepository
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
            R.id.action_sync_bookshelf -> syncBookShelf()
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

    /**
     *
     */
    private fun syncBookShelf() {
        // fetch book data
        val query = BmobQuery<CollBookBmobBean>()
        query.findObjects(object : FindListener<CollBookBmobBean>() {
            override fun done(result: MutableList<CollBookBmobBean>?, e: BmobException?) {
                if (e != null) {
                    toast("${e.errorCode}: ${e.message}")
                    Log.e("tag", "${e.errorCode}: ${e.message}")
                }
                val localBooks = BookRepository.instance.collBooks.map { BmobDaoUtils.dao2Bmob(it) }
                val allBooks = if (result != null) localBooks.union(result) else localBooks
                Log.i("tag", "service's book size = ${result?.size}")
                Log.i("tag", "local's book size = ${localBooks.size}")
                Log.i("tag", "all book size = ${allBooks.size}")
                if (allBooks.isNotEmpty() && (allBooks.size != localBooks.size || !allBooks.containsAll(localBooks))) {
                    Log.e("tag", "update local")
                    val localAllBooks = allBooks.map { BmobDaoUtils.bmob2Dao(it) }
                    BookRepository.instance.saveCollBooks(localAllBooks)
                    RxBus.getInstance().post(SyncBookEvent())
                }
                if (allBooks.isNotEmpty()) {
                    val insertBooks = allBooks.subtract(result!!)
                    val updateBooks = allBooks.filter {
                        val relateBook = result.firstOrNull { book -> it.id == book.id }
                        if (relateBook != null) {
                            it.objectId = relateBook.objectId
                            true
                        } else {
                            false
                        }
                    }
                    Log.e("tag", "update service: insert ${insertBooks.size} books, update ${updateBooks.size} books")
                    val batch = BmobBatch()
                    batch.insertBatch(insertBooks.toList())
                    batch.updateBatch(updateBooks)
                    batch.doBatch(object : QueryListListener<BatchResult>() {
                        override fun done(result: MutableList<BatchResult>?, e: BmobException?) {
                            if (e == null) {
                                result?.forEach {
                                    it.error?.let {e ->
                                        toast("${e.errorCode}: ${e.message}")
                                        Log.e("tag", "${e.errorCode}: ${e.message}")
                                    }
                                }
                            } else {
                                toast("${e.errorCode}: ${e.message}")
                                Log.e("tag", "${e.errorCode}: ${e.message}")
                            }

                        }
                    })
                }

            }
        })
    }



}

fun main() {
    val list1 = listOf("one", "two")
    val list2 = listOf("three", "one")
    val result = list1.union(list2)
    print(result)
}