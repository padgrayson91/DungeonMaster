package com.tendebit.dungeonmastercore

import com.tendebit.dungeonmastercore.debug.DebugUtils
import org.junit.Test

class TestDebugUtils {

	@Test
	fun testIsTestRunning() {
		assert(DebugUtils.isRunningTest())
	}

}