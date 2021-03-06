package com.tendebit.dungeonmaster.charactercreation3.feature.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tendebit.dungeonmaster.charactercreation3.ability.view.DndAbilitySelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.characterclass.view.DndClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.feature.viewmodel.CharacterCreationSectionsViewModel
import com.tendebit.dungeonmaster.charactercreation3.proficiency.view.DndProficiencyGroupFragment
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyGroupViewModel
import com.tendebit.dungeonmaster.charactercreation3.race.view.DndRaceSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.race.viewmodel.DndRaceSelectionViewModel
import com.tendebit.dungeonmastercore.platform.ViewModelManager
import io.reactivex.disposables.Disposable

class CharacterCreationSectionsAdapter(fragment: Fragment, private val viewModel: CharacterCreationSectionsViewModel, private val viewModelManager: ViewModelManager) : FragmentStateAdapter(fragment) {

	private val additionsDisposable: Disposable = viewModel.pageAdditions.subscribe {
		notifyItemInserted(it)
	}
	private val removalsDisposable: Disposable = viewModel.pageRemovals.subscribe {
		notifyItemRemoved(it)
	}

	override fun createFragment(position: Int): Fragment {
		return when (val childViewModel = viewModel.pages[position]) {
			is DndProficiencyGroupViewModel -> DndProficiencyGroupFragment.newInstance(viewModelManager.addViewModel(childViewModel))
			is DndCharacterClassSelectionViewModel -> DndClassSelectionFragment.newInstance(viewModelManager.addViewModel(childViewModel))
			is DndRaceSelectionViewModel -> DndRaceSelectionFragment.newInstance(viewModelManager.addViewModel(childViewModel))
			is DndAbilitySelectionViewModel -> DndAbilitySelectionFragment.newInstance(viewModelManager.addViewModel(childViewModel))
			else -> throw IllegalStateException("Had unexpected viewmodel $childViewModel")
		}
	}

	override fun getItemCount(): Int = viewModel.pageCount

	fun dispose() {
		additionsDisposable.dispose()
		removalsDisposable.dispose()
	}

}
