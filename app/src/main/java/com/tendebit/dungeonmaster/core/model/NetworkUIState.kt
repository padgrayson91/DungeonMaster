package com.tendebit.dungeonmaster.core.model

interface NetworkUIState {
    var activeNetworkCalls: Int

    fun onNetworkCallStart() {
        synchronized(this) {
            activeNetworkCalls++
        }
    }

    fun onNetworkCallFinish() {
        synchronized(this) {
            activeNetworkCalls--
        }
    }
}