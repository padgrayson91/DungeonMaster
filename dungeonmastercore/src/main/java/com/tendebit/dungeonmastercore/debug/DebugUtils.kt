package com.tendebit.dungeonmastercore.debug

import java.util.concurrent.atomic.AtomicBoolean

object DebugUtils {

	val logger = Logger("GLOBAL", debug = false)
	private var isRunningTest: AtomicBoolean? = null

	@Suppress("SpellCheckingInspection")
	fun isRunningTest(): Boolean {
		synchronized(this) {
			if (isRunningTest == null) {
				val isTest = try {
					Class.forName("com.tendebit.dungeonmastercore.TestDebugUtils")
					true
				} catch (ex: ClassNotFoundException) {
					false
				}
				isRunningTest = AtomicBoolean(isTest)
			}

			return isRunningTest!!.get()
		}
	}

}
