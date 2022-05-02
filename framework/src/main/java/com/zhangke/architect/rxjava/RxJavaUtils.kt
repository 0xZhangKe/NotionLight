package com.zhangke.architect.rxjava

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class DisposableHelper {
    private var compositeDisposable: CompositeDisposable? = null
    private var active = false

    fun activate() {
        active = true
    }

    fun deactivate() {
        active = false
        compositeDisposable?.dispose()
        compositeDisposable = null
    }

    fun add(d: Disposable) {
        if (active) {
            var container = compositeDisposable
            if (container == null) {
                container = CompositeDisposable()
                compositeDisposable = container
            }
            container.add(d)
        } else {
            d.dispose()
        }
    }
}

fun Disposable.attachToLifecycle(lifecycle: Lifecycle?) {
    if (lifecycle == null) {
        return
    }
    val disposableHelper = DisposableHelper().apply {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            activate()
        }
    }
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            disposableHelper.activate()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            disposableHelper.deactivate()
        }
    })
    disposableHelper.add(this)
}
