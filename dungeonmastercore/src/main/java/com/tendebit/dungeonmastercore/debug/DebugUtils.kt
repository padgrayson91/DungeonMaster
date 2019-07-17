package com.tendebit.dungeonmastercore.debug

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

object DebugUtils {

	val logger = Logger("GLOBAL", debug = false)
	private var isRunningTest: AtomicBoolean? = null

	@Suppress("SpellCheckingInspection")
	fun isRunningTest(): Boolean {
		synchronized(this) {
			if (isRunningTest == null) {
				val isTest = try {
					Log.getStackTraceString(RuntimeException())
					false
				} catch (ex: RuntimeException) {
					true
				}
				isRunningTest = AtomicBoolean(isTest)
			}

			return isRunningTest!!.get()
		}
	}

}
