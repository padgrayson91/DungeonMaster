package com.tendebit.dungeonmaster.charactercreation3.proficiency.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.ChipGroup
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.MultiSelectViewModel
import io.reactivex.disposables.Disposable
import java.text.MessageFormat

class DndProficiencyGroupFragment : Fragment() {

	private lateinit var chipGroup: ChipGroup
	private lateinit var instructions: TextView
	private var internalViewModel: MultiSelectViewModel? = null
	var viewModel: MultiSelectViewModel?
		get() = internalViewModel
		set(value) { onAttachViewModel(value) }
	private var disposable: Disposable? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_proficiency_selection, container, false)
		chipGroup = root.findViewById(R.id.proficiency_chips)
		instructions = root.findViewById(R.id.instructions)
		return root
	}

	private fun onAttachViewModel(viewModel: MultiSelectViewModel?) {
		subscribeToViewModel(viewModel)
		internalViewModel = viewModel
		if (viewModel != null) {
			createChildViews(viewModel)
			updateFromViewModel(viewModel)
		}
	}

	private fun subscribeToViewModel(viewModel: MultiSelectViewModel?) {
		disposable = viewModel?.changes?.subscribe { updateFromViewModel(it) }
	}

	override fun onResume() {
		super.onResume()
		subscribeToViewModel(viewModel)
	}

	override fun onPause() {
		super.onPause()
		disposable?.dispose()
	}

	private fun updateFromViewModel(viewModel: MultiSelectViewModel) {
		instructions.text = MessageFormat.format(getText(R.string.proficiency_selection).toString(),
				viewModel.remainingChoices)
	}

	private fun createChildViews(viewModel: MultiSelectViewModel) {
		chipGroup.removeAllViews() // FIXME: should recycle views by updating the ViewModels
		viewModel.children.forEach {
			chipGroup.addView(DndProficiencyViewWrapper(context, it).getView())
		}
	}

}