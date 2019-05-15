package com.tendebit.dungeonmaster.core.platform

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.tendebit.dungeonmaster.core.extensions.getViewModelManager

/**
 * Singleton used to access a [ViewModelManager] instance for a given [Activity]. The default implementation assumes
 * that a [FragmentActivity] is being used, which provides a [ViewModelManager] via [FragmentActivity.getViewModelManager]
 */
object ViewModels {

	private val defaultViewModelAccess: (Activity?) -> ViewModelManager? = { (it as? FragmentActivity)?.getViewModelManager() }

	var viewModelAccess: ((activity: Activity?) -> ViewModelManager?) = defaultViewModelAccess

	fun from(activity: Activity?): ViewModelManager? = viewModelAccess(activity)

}
