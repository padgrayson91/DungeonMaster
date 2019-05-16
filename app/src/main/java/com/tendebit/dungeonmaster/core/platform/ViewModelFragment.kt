package com.tendebit.dungeonmaster.core.platform

import android.os.Bundle
import android.util.LongSparseArray
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.core.viewmodel3.Clearable
import com.tendebit.dungeonmaster.core.viewmodel3.ViewModel
import com.tendebit.dungeonmaster.core.viewmodel3.ViewModelFactory

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

	private val viewModels = LongSparseArray<ViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	override fun onDestroy() {
		super.onDestroy()
		for (i in 0 until viewModels.size()) {
			(viewModels[viewModels.keyAt(i)] as? Clearable)?.clear()
		}
	}

	override fun addViewModel(viewModel: ViewModel): Long {
		val key = if (viewModels.size() == 0) 0 else viewModels.keyAt(viewModels.size() - 1) + 1
		viewModels.put(key, viewModel)
		return key
	}

	override fun removeViewModel(id: Long?) {
		if (id == null) return
		viewModels.remove(id)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> findViewModel(id: Long?): T? {
		if (id == null) return null
		return viewModels[id] as? T
	}

	override fun <T : ViewModel> findOrCreateViewModel(id: Long?, factory: ViewModelFactory<T>): ViewModelManager.Lookup<T> {
		val existing = findViewModel<T>(id)
		if (existing != null && id != null) return ViewModelManager.Lookup(id, existing)
		val created = factory.createNew()
		val createdId = addViewModel(created)
		return ViewModelManager.Lookup(createdId, created)
	}

}
