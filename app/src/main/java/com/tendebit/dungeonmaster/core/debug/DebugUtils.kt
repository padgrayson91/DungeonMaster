package com.tendebit.dungeonmaster.core.debug

import java.util.concurrent.atomic.AtomicBoolean

object DebugUtils {

	val logger = Logger("GLOBAL")
	private var isRunningTest: AtomicBoolean? = null

	fun isRunningTest(): Boolean {
		synchronized(this) {
			if (isRunningTest == null) {
				val isTest = try {
					Class.forName("com.tendebit.dungeonmaster.charactercreation3.characterclass.TestDndCharacterClassSelection")
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
