package com.tendebit.dungeonmaster.core.concurrency

interface Concurrency {

	fun runImmediate(runnable: () -> Unit)

	fun runCalculation(runnable: suspend () -> Unit, onComplete: (() -> Unit)? = null)

	fun runDiskOrNetwork(runnable: suspend () -> Unit, onComplete: (() -> Unit)? = null)

}
