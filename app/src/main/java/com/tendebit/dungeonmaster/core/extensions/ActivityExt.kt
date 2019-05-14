package com.tendebit.dungeonmaster.core.extensions

import androidx.fragment.app.FragmentActivity
import com.tendebit.dungeonmaster.core.platform.VIEW_MODEL_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.platform.ViewModelFragment
import com.tendebit.dungeonmaster.core.platform.ViewModelManager

fun FragmentActivity.getViewModelManager(): ViewModelManager {
	val existing = supportFragmentManager.findFragmentByTag(VIEW_MODEL_FRAGMENT_TAG) as? ViewModelManager
	if (existing != null) return existing

	val created = ViewModelFragment.newInstance()
	supportFragmentManager.beginTransaction()
			.add(created, VIEW_MODEL_FRAGMENT_TAG)
			.commit()
	return created
}
