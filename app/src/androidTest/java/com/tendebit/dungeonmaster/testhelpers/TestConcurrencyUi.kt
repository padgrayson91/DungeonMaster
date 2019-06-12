package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
object TestConcurrencyUi : Concurrency {

	val scope = CoroutineScope(Dispatchers.Unconfined)

	override fun runImmediate(runnable: () -> Unit) {
		runnable()
	}

	override fun runCalculation(runnable: suspend () -> Unit, onComplete: (() -> Unit)?) {
		scope.launch { runnable(); onComplete?.invoke(); }
	}

	override fun runDiskOrNetwork(runnable: suspend () -> Unit, onComplete: (() -> Unit)?) {
		runCalculation(runnable, onComplete)
	}
}