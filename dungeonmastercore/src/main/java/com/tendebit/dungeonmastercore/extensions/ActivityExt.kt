package com.tendebit.dungeonmastercore.extensions

import androidx.fragment.app.FragmentActivity
import com.tendebit.dungeonmastercore.debug.DebugUtils
import com.tendebit.dungeonmastercore.platform.VIEW_MODEL_FRAGMENT_TAG
import com.tendebit.dungeonmastercore.platform.ViewModelFragment
import com.tendebit.dungeonmastercore.platform.ViewModelManager

fun FragmentActivity.getViewModelManager(): ViewModelManager {
		DebugUtils.logger.writeDebug("Trying to get ViewModelManager from FragmentManager")
		val existing = supportFragmentManager.findFragmentByTag(VIEW_MODEL_FRAGMENT_TAG) as? ViewModelManager
		if (existing != null) return existing

		val created = ViewModelFragment.newInstance()
		supportFragmentManager.beginTransaction()
				.add(created, VIEW_MODEL_FRAGMENT_TAG)
				.commit()
		DebugUtils.logger.writeDebug("Did not have existing fragment, had to make a new one")
		return created
}
