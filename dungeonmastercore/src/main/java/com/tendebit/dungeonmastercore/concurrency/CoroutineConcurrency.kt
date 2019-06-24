package com.tendebit.dungeonmastercore.concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoroutineConcurrency(private val scope: CoroutineScope) : Concurrency {

	override fun runImmediate(runnable: () -> Unit) {
		scope.launch(context = Dispatchers.Main) {
			runnable()
		}
	}

	override fun runCalculation(runnable: suspend () -> Unit, onComplete: (() -> Unit)?) {
		scope.launch(context = Dispatchers.Main) {
			withContext(Dispatchers.Default) {
				runnable()
			}
			onComplete?.invoke()
		}
	}

	override fun runDiskOrNetwork(runnable: suspend () -> Unit, onComplete: (() -> Unit)?) {
		scope.launch(context = Dispatchers.Main) {
			withContext(Dispatchers.IO) {
				runnable()
			}
			onComplete?.invoke()
		}
	}

}
