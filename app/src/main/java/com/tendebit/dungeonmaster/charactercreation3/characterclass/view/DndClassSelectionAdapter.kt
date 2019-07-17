package com.tendebit.dungeonmaster.charactercreation3.characterclass.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmastercore.view.adapter.SingleSelectAdapter
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel

class DndClassSelectionAdapter(private val viewModel: SingleSelectViewModel<DndCharacterClass>?) : SingleSelectAdapter<DndCharacterClass, DndClassViewHolder>(viewModel) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DndClassViewHolder {
		return DndClassViewHolder(LayoutInflater.from(parent.context), parent)
	}

	override fun onBindViewHolder(holder: DndClassViewHolder, position: Int) {
		holder.populate(viewModel!!.children[position])
	}

}
