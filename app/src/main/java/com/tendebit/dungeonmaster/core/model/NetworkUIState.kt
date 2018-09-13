package com.tendebit.dungeonmaster.core.model

import io.reactivex.subjects.Subject

interface NetworkUIState {
    var activeNetworkCalls: Int
    val networkCallChanges: Subject<Int>

    fun onNetworkCallStart() {
        synchronized(this) {
            activeNetworkCalls++
            networkCallChanges.onNext(activeNetworkCalls)
        }
    }

    fun onNetworkCallFinish() {
        synchronized(this) {
            activeNetworkCalls--
            networkCallChanges.onNext(activeNetworkCalls)
        }
    }

    fun cancelAllCalls()
}