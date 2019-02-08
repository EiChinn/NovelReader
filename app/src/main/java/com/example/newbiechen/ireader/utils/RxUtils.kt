package com.example.newbiechen.ireader.utils

import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.DetailBean
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

object RxUtils {
    @JvmStatic
    fun <T> toSimpleSingle(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    @JvmStatic
    fun <T> toCommentDetail(detailSingle: Single<T>, bestCommentsSingle: Single<List<CommentBean>>, commentsSingle: Single<List<CommentBean>>): Single<DetailBean<T>> {
        return Single.zip(detailSingle, bestCommentsSingle, commentsSingle, object : Function3<T, List<CommentBean>, List<CommentBean>, DetailBean<T>> {
            override fun apply(t1: T, t2: List<CommentBean>, t3: List<CommentBean>): DetailBean<T> {
                return DetailBean(t1, t2, t3)
            }

        })
    }

}