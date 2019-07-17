package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.tendebit.dungeonmastercore.view.adapter.SingleSelectAdapter
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel

class DndAbilityDiceRollAdapter(private val viewModel: SingleSelectViewModel<Int>?) : SingleSelectAdapter<Int, DndAbilityDiceRollViewHolder>(viewModel) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DndAbilityDiceRollViewHolder {
		return DndAbilityDiceRollViewHolder(LayoutInflater.from(parent.context), parent)
	}

	override fun onBindViewHolder(holder: DndAbilityDiceRollViewHolder, position: Int) {
		holder.populate(viewModel!!.children[position])
	}

}
