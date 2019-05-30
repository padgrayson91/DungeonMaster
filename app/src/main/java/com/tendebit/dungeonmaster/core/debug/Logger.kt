package com.tendebit.dungeonmaster.core.debug

import android.util.Log
import com.tendebit.dungeonmaster.BuildConfig
import java.lang.StringBuilder
import kotlin.math.min

class Logger(vararg tags: CharSequence) {

	private val tag = mergeTags(tags)

	private fun mergeTags(tags: Array<out CharSequence>): String {
		val builder = StringBuilder()
		tags.forEach { builder.append("($it)") }
		return builder.toString().substring(0 until min(23, builder.length)) // Android max tag length
	}

	fun writeError(text: CharSequence, throwable: Throwable? = null) {
		if (DebugUtils.isRunningTest()) {
			return
		}
		Log.e(tag, text.toString(), throwable)
	}

	fun write(text: CharSequence) {
		if (DebugUtils.isRunningTest()) {
			return
		}
		Log.d(tag, text.toString())
	}

	fun writeDebug(text: CharSequence) {
		if (!BuildConfig.DEBUG) {
			return
		}
		if (DebugUtils.isRunningTest()) {
			return
		}

		Log.d(tag, text.toString())
	}

}
