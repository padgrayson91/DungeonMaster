package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ID_KEY
import com.tendebit.dungeonmastercore.platform.ViewModels
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel

class DndAbilityDiceRollSelectionFragment : Fragment() {

	companion object {
		fun newInstance(viewModelId: Long) = DndAbilityDiceRollSelectionFragment().apply { arguments = Bundle().apply { putLong(ID_KEY, viewModelId) } }
	}

	private lateinit var recycler: RecyclerView
	private var adapter: DndAbilityDiceRollAdapter? = null
	private var viewModel: SingleSelectViewModel<Int>? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.small_recycler, container, false)
		recycler = root.findViewById(R.id.small_recycler)
		recycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
		return root
	}

	override fun onResume() {
		super.onResume()
		val oldViewModel = viewModel
		viewModel = ViewModels.from(activity)?.findViewModel(arguments?.getLong(ID_KEY))
		if (viewModel != oldViewModel) {
			onAttachViewModel(viewModel)
		}
		adapter?.resume()
		recycler.adapter = adapter
	}

	override fun onPause() {
		super.onPause()
		adapter?.clear()
	}

	private fun onAttachViewModel(viewModel: SingleSelectViewModel<Int>?) {
		adapter?.clear()
		adapter = DndAbilityDiceRollAdapter(viewModel)
	}

}
