package com.tendebit.dungeonmaster.core.debug

import android.util.Log
import com.tendebit.dungeonmaster.BuildConfig
import kotlin.math.min

@Suppress("unused")
class Logger(vararg tags: CharSequence) {

	private val tag = mergeTags(tags)

	private fun mergeTags(tags: Array<out CharSequence>): String {
		val builder = StringBuilder()
		tags.forEach { builder.append("($it)") }
		return builder.toString().substring(0 until min(23, builder.length)) // Android max tag length
	}

	fun writeError(text: CharSequence, throwable: Throwable? = null) {
		if (DebugUtils.isRunningTest()) {
			writeForJUnit(text, throwable)
			return
		}
		Log.e(tag, text.toString(), throwable)
	}

	fun write(text: CharSequence) {
		if (DebugUtils.isRunningTest()) {
			writeForJUnit(text)
			return
		}
		Log.d(tag, text.toString())
	}

	fun writeDebug(text: CharSequence) {
		if (!BuildConfig.DEBUG) {
			return
		}
		if (DebugUtils.isRunningTest()) {
			writeForJUnit(text)
			return
		}

		Log.d(tag, text.toString())
	}

	private fun writeForJUnit(text: CharSequence, throwable: Throwable? = null) {
		val throwableText = if (throwable == null) {
			""
		} else {
			": $throwable"
		}
		System.out.println("$tag $text$throwableText")
	}

}
