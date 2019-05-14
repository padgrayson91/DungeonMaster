package com.tendebit.dungeonmaster.core.platform

import androidx.fragment.app.FragmentActivity
import com.tendebit.dungeonmaster.core.extensions.getViewModelManager

object ViewModels {

	private val defaultViewModelAccess: (FragmentActivity?) -> ViewModelManager? = { it?.getViewModelManager() }

	var viewModelAccess: ((activity: FragmentActivity?) -> ViewModelManager?) = defaultViewModelAccess

	fun from(activity: FragmentActivity?): ViewModelManager? = viewModelAccess(activity)

}
