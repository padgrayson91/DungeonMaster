package com.tendebit.dungeonmaster.core.model

import com.tendebit.dungeonmaster.charactercreation.AttachableViewModel
import io.reactivex.subjects.Subject

/**
 * This interface represents a ViewModel that maintains a count of waiting asynchronous calls
 */
interface AsyncViewModel : AttachableViewModel {
    // TODO: I think it would be good to maintain some information about the active calls rather than just a count
    var activeAsyncCalls: Int
    val asyncCallChanges: Subject<Int>

    fun onAsyncCallStart() {
        synchronized(this) {
            activeAsyncCalls++
            asyncCallChanges.onNext(activeAsyncCalls)
        }
    }

    fun onAsyncCallFinish() {
        synchronized(this) {
            activeAsyncCalls--
            asyncCallChanges.onNext(activeAsyncCalls)
        }
    }
}