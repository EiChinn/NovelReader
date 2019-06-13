package com.example.newbiechen.ireader.ui.activity

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.DownloadTaskBean
import com.example.newbiechen.ireader.service.DownloadService
import com.example.newbiechen.ireader.ui.adapter.DownLoadAdapter
import com.example.newbiechen.ireader.ui.base.BaseActivity
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_refresh_list.*

/**
 * Created by newbiechen on 17-5-11.
 * 下载面板
 */

class DownloadActivity : BaseActivity(), DownloadService.OnDownloadListener {
    private var mDownloadAdapter: DownLoadAdapter? = null

    private var mConn: ServiceConnection? = null
    private var mService: DownloadService.IDownloadManager? = null
    override fun getContentId(): Int {
        return R.layout.activity_refresh_list
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "下载列表"
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mDownloadAdapter = DownLoadAdapter()
        refresh_rv_content!!.addItemDecoration(DividerItemDecoration(this))
        refresh_rv_content!!.layoutManager = LinearLayoutManager(this)
        refresh_rv_content!!.adapter = mDownloadAdapter
    }

    override fun initClick() {
        super.initClick()
        mDownloadAdapter!!.setOnItemClickListener { view, pos ->
            //传递信息
            val bean = mDownloadAdapter!!.getItem(pos)
            when (bean.status) {
                //准备暂停
                DownloadTaskBean.STATUS_LOADING -> mService!!.setDownloadStatus(bean.taskName, DownloadTaskBean.STATUS_PAUSE)
                //准备暂停
                DownloadTaskBean.STATUS_WAIT -> mService!!.setDownloadStatus(bean.taskName, DownloadTaskBean.STATUS_PAUSE)
                //准备启动
                DownloadTaskBean.STATUS_PAUSE -> mService!!.setDownloadStatus(bean.taskName, DownloadTaskBean.STATUS_WAIT)
                //准备启动
                DownloadTaskBean.STATUS_ERROR -> mService!!.setDownloadStatus(bean.taskName, DownloadTaskBean.STATUS_WAIT)
            }
        }
    }

    override fun processLogic() {
        super.processLogic()

        mConn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                mService = service as DownloadService.IDownloadManager
                //添加数据到队列中
                mDownloadAdapter!!.addItems(mService!!.downloadTaskList)

                mService!!.setOnDownloadListener(this@DownloadActivity)

                refresh_layout!!.showFinish()
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        //绑定
        bindService(Intent(this, DownloadService::class.java), mConn!!, Service.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mConn?.let {
            unbindService(it)
        }
    }

    override fun onDownloadChange(pos: Int, status: Int, msg: String) {
        val bean = mDownloadAdapter!!.getItem(pos)
        bean.status = status
        if (DownloadTaskBean.STATUS_LOADING == status) {
            bean.currentChapter = Integer.valueOf(msg)
        }
        mDownloadAdapter!!.notifyItemChanged(pos)
    }

    override fun onDownloadResponse(pos: Int, status: Int) {
        val bean = mDownloadAdapter!!.getItem(pos)
        bean.status = status
        mDownloadAdapter!!.notifyItemChanged(pos)
    }
}
