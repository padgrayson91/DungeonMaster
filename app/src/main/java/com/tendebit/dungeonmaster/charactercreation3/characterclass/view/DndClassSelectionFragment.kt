package com.tendebit.dungeonmaster.charactercreation3.characterclass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.SingleSelectViewModel
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter

class DndClassSelectionFragment : Fragment() {

	private lateinit var recycler: RecyclerView
	private var adapter: DndClassSelectionAdapter? = null
	private var internalViewModel: SingleSelectViewModel? = null
	var viewModel: SingleSelectViewModel?
		get() = internalViewModel
		set(value) { onAttachViewModel(value) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
		recycler = root.findViewById(R.id.item_list)
		recycler.layoutManager = LinearLayoutManager(activity)
		return root
	}


	override fun onResume() {
		super.onResume()
		adapter?.resume()
	}

	override fun onPause() {
		super.onPause()
		adapter?.clear()
	}

	private fun onAttachViewModel(viewModel: SingleSelectViewModel?) {
		adapter?.clear()
		adapter = DndClassSelectionAdapter(viewModel)
		recycler.adapter = adapter
	}

}
