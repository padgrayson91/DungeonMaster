package com.tendebit.dungeonmaster.charactercreation3.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tendebit.dungeonmaster.charactercreation3.characterclass.view.DndClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.proficiency.view.DndProficiencyGroupFragment
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyGroupViewModel
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.CharacterCreationSectionsViewModel
import com.tendebit.dungeonmaster.core.platform.ViewModelManager
import io.reactivex.disposables.Disposable

class CharacterCreationSectionsAdapter(fragment: Fragment, private val viewModel: CharacterCreationSectionsViewModel, viewModelManager: ViewModelManager) : FragmentStateAdapter(fragment) {

	private val additionsDisposable: Disposable = viewModel.pageAdditions.subscribe {
		notifyItemInserted(it)
	}
	private val removalsDisposable: Disposable = viewModel.pageRemovals.subscribe {
		notifyItemRemoved(it)
	}

	private val viewModelIds = ArrayList<Long>(viewModel.pageCount)

	init {
		for (child in viewModel.pages) {
			viewModelIds.add(viewModelManager.addViewModel(child))
		}
	}

	override fun getItem(position: Int): Fragment {
		return when (val childViewModel = viewModel.pages[position]) {
			is DndProficiencyGroupViewModel -> DndProficiencyGroupFragment.newInstance(viewModelIds[position])
			is DndCharacterClassSelectionViewModel -> DndClassSelectionFragment.newInstance(viewModelIds[position])
			else -> throw IllegalStateException("Had unexpected viewmodel $childViewModel")
		}
	}

	override fun getItemCount(): Int = viewModel.pageCount

	fun dispose() {
		additionsDisposable.dispose()
		removalsDisposable.dispose()
	}

}
