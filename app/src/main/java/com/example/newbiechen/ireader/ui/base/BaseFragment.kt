package com.example.newbiechen.ireader.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {
    private lateinit var mDisposable: CompositeDisposable

    private lateinit var root: View
    private lateinit var unbinder: Unbinder

    @LayoutRes
    protected abstract fun getContentId(): Int

    protected fun addDisposable(d: Disposable) {
        if (!this::mDisposable.isInitialized) {
            mDisposable = CompositeDisposable()
        }
        mDisposable.add(d)
    }

    protected open fun initData(savedInstanceState: Bundle?) {}
    protected open fun initWidget(savedInstanceState: Bundle?) {}
    protected open fun initClick() {}
    protected open fun processLogic() {}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(getContentId(), container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(savedInstanceState)
        unbinder = ButterKnife.bind(this, root)
        initWidget(savedInstanceState)
        initClick()
        processLogic()

    }

    override fun onDetach() {
        if (this::unbinder.isInitialized) {
            unbinder.unbind()
        }
        if (this::mDisposable.isInitialized) {
            mDisposable.clear()
        }
        super.onDetach()

    }

    fun getName(): String = javaClass.name




}