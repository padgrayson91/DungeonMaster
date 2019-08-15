package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySelectionViewModel

class DndAbilitySlotFragment : Fragment() {

	companion object {
		fun newInstance() = DndAbilitySlotFragment()
	}

	private var recycler: RecyclerView? = null
	private var adapter: DndAbilitySlotAdapter? = null
	var viewModel: DndAbilitySelectionViewModel? = null
		set(value) { field = value; onAttachViewModel(field) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.small_recycler, container, false)
		recycler = root.findViewById(R.id.small_recycler)
		recycler?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
		return root
	}

	override fun onPause() {
		super.onPause()
		adapter?.clear()
		recycler?.adapter = adapter
	}

	private fun onAttachViewModel(viewModel: DndAbilitySelectionViewModel?) {
		adapter?.clear()
		adapter = DndAbilitySlotAdapter(viewModel)
		recycler?.adapter = adapter

	}

}