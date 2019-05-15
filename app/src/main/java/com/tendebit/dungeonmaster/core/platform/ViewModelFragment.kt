package com.tendebit.dungeonmaster.core.platform

import android.os.Bundle
import android.util.LongSparseArray
import androidx.fragment.app.Fragment

const val VIEW_MODEL_FRAGMENT_TAG = "view_model_fragment"

/**
 * An implementation of [ViewModelManager] as a headless [Fragment], which can be attached to an activity.
 * NOTE: This should not be nested within another fragment to avoid issues with older Android APIs, which forbid
 * using nested headless fragments
 */
class ViewModelFragment : Fragment(), ViewModelManager {

	companion object {

		fun newInstance(): ViewModelFragment {
			return ViewModelFragment()
		}

	}

	private val viewModels = LongSparseArray<Any>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	override fun addViewModel(viewModel: Any): Long {
		val key = if (viewModels.size() == 0) 0 else viewModels.keyAt(viewModels.size() - 1) + 1
		viewModels.put(key, viewModel)
		return key
	}

	override fun removeViewModel(id: Long?) {
		if (id == null) return
		viewModels.remove(id)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T> findViewModel(id: Long?): T? {
		if (id == null) return null
		return viewModels[id] as? T
	}

}
