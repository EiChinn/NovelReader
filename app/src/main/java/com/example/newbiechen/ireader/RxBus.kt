package com.example.newbiechen.ireader

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by ei_chinn on 17-4-18.
 * 原理:PublishSubject本身作为观察者和被观察者。
 */
class RxBus {
    private val mEventBus: PublishSubject<Any> = PublishSubject.create()
    companion object {

        @Volatile private var instance: RxBus? = null

        @JvmStatic
        fun getInstance(): RxBus {
            if (instance == null) {
                synchronized(RxBus::class.java) {
                    if (instance == null) {
                        instance = RxBus()
                    }
                }
            }

            return instance!!
        }

    }

    /**
     * 发送事件(post event)
     * @param event : event object(事件的内容)
     */
    fun post(event: Any) {
        mEventBus.onNext(event)
    }

    /**
     *
     * @param code
     * @param event
     */
    fun post(code: Int, event: Any) {
        mEventBus.onNext(Message(code, event))

    }

    class Message(val code: Int, val event: Any)

    /**
     * 返回Event的管理者,进行对事件的接受
     * @return
     */
    fun toObservable() = mEventBus

    /**
     *
     * @param cls :保证接受到制定的类型
     * @param <T>
     * @return
     */
    fun <T> toObservable(cls: Class<T>): Observable<T> {
        return mEventBus.ofType(cls)
    }

    fun <T> toObservable(code: Int, cls: Class<T>): Observable<T> {
        return mEventBus.ofType(Message::class.java)
                .filter{ msg-> msg.code == code &&  cls.isInstance(msg.event)}
                .map { msg -> msg.event as T}
    }
}