package com.example.androidlogin.`object`

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

object RxBus {
    private val pushlisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        pushlisher.onNext(event)
    }
    fun <T> listen(eventType: Class<T>): Observable<T> = pushlisher.ofType(eventType)
}