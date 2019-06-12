package com.tendebit.dungeonmaster.core.platform

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.tendebit.dungeonmaster.core.debug.DebugUtils
import com.tendebit.dungeonmaster.core.extensions.getViewModelManager
import java.util.WeakHashMap

/**
 * Singleton used to access a [ViewModelManager] instance for a given [Activity]. The default implementation assumes
 * that a [FragmentActivity] is being used, which provides a [ViewModelManager] via [FragmentActivity.getViewModelManager]
 */
object ViewModels {

	private val defaultViewModelAccess: (Activity?) -> ViewModelManager? = { (it as? FragmentActivity)?.getViewModelManager() }

	var viewModelAccess: ((activity: Activity?) -> ViewModelManager?) = defaultViewModelAccess
	private val cache = WeakHashMap<Activity, ViewModelManager>()

	fun from(activity: Activity?): ViewModelManager? {
		DebugUtils.logger.writeDebug("Fetching ViewModelManager for $activity")
		return cache[activity] ?: fetchNewInstanceAndCache(activity)
	}

	private fun fetchNewInstanceAndCache(activity: Activity?): ViewModelManager? {
		val accessedInstance = viewModelAccess(activity)
		if (activity != null) {
			cache[activity] = accessedInstance
		}
		return accessedInstance
	}

}
