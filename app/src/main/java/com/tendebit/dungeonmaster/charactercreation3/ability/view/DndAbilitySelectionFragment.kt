package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ID_KEY
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySelectionViewModel
import com.tendebit.dungeonmastercore.platform.ViewModels
import com.tendebit.dungeonmastercore.view.LoadingDialog
import io.reactivex.disposables.Disposable

private const val ROLLS_FRAGMENT_TAG = "fragment_rolls"
private const val SLOTS_FRAGMENT_TAG = "fragment_slots"

class DndAbilitySelectionFragment : Fragment() {

	companion object {
		fun newInstance(viewModelId: Long) = DndAbilitySelectionFragment().apply { arguments = Bundle().apply { putLong(ID_KEY, viewModelId) } }
	}

	private lateinit var loadingDialog: LoadingDialog
	private lateinit var fragmentContainer: ViewGroup
	private var slotsFragment: DndAbilitySlotFragment? = null
	private var rollsFragment: DndAbilityDiceRollSelectionFragment? = null
	private var disposable: Disposable? = null
	private var viewModel: DndAbilitySelectionViewModel? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_ability_selection, container, false)
		loadingDialog = root.findViewById(R.id.loading_dialog)
		fragmentContainer = root.findViewById(R.id.fragment_container)
		rollsFragment = childFragmentManager.findFragmentByTag(ROLLS_FRAGMENT_TAG) as? DndAbilityDiceRollSelectionFragment
		if (rollsFragment== null) {
			val newInstance = DndAbilityDiceRollSelectionFragment.newInstance()
			childFragmentManager.beginTransaction()
					.add(R.id.fragment_container, newInstance, ROLLS_FRAGMENT_TAG)
					.commit()
			rollsFragment = newInstance
		}
		rollsFragment?.viewModel = viewModel?.rolls
		slotsFragment = childFragmentManager.findFragmentByTag(SLOTS_FRAGMENT_TAG) as? DndAbilitySlotFragment
		if (slotsFragment == null) {
			val newInstance = DndAbilitySlotFragment.newInstance()
			childFragmentManager.beginTransaction()
					.add(R.id.fragment_container, newInstance, SLOTS_FRAGMENT_TAG)
					.commit()
			slotsFragment = newInstance
		}
		slotsFragment?.viewModel = viewModel
		return root
	}

	override fun onResume() {
		super.onResume()
		val oldViewModel = viewModel
		viewModel = ViewModels.from(activity)?.findViewModel(arguments?.getLong(ID_KEY))
		if (viewModel != oldViewModel) {
			onAttachViewModel(viewModel)
		}
		disposable = viewModel?.changes?.subscribe { onViewModelChanged(it) }
		onViewModelChanged(viewModel)
	}

	override fun onPause() {
		super.onPause()
		disposable?.dispose()
	}

	private fun onAttachViewModel(viewModel: DndAbilitySelectionViewModel?) {
		disposable?.dispose()
		slotsFragment?.viewModel = viewModel
	}

	private fun onViewModelChanged(viewModel: DndAbilitySelectionViewModel?) {
		loadingDialog.visibility = if (viewModel?.showLoading == false) View.GONE else View.VISIBLE
		rollsFragment?.viewModel = viewModel?.rolls

	}

}
