package com.example.newbiechen.ireader.ui.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.newbiechen.ireader.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mDisposable: CompositeDisposable
    private var mToolbar: Toolbar? = null

    /****************************abstract area*************************************/
    @LayoutRes
    protected abstract fun getContentId(): Int

    /************************init area************************************/
    protected fun addDisposable(disposable: Disposable) {
        if (!this::mDisposable.isInitialized) {
            mDisposable = CompositeDisposable()
        }
        mDisposable.add(disposable)
    }

    private fun initToolbar() {
        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.let {
            supportActionBar(mToolbar!!)
            setUpToolbar(mToolbar!!)
        }
    }

    private fun supportActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    /**
     * 配置Toolbar
     * @param toolbar
     */
    protected open fun setUpToolbar(toolbar: Toolbar) {}

    protected open fun initData(savedInstanceState: Bundle?) {}

    /**
     * 初始化零件
     */
    protected open fun initWidget() {}

    /**
     * 初始化点击事件
     */
    protected open fun initClick() {}

    /**
     * 逻辑使用区
     */
    protected open fun processLogic() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("tag", "Base onCreate")
        super.onCreate(savedInstanceState)
        setContentView(getContentId())
        initData(savedInstanceState)
        initToolbar()
        initWidget()
        initClick()
        processLogic()
    }

    override fun onDestroy() {
        if (this::mDisposable.isInitialized) {
            mDisposable.dispose()
        }
        super.onDestroy()
    }

    /**************************used method area*******************************************/
}