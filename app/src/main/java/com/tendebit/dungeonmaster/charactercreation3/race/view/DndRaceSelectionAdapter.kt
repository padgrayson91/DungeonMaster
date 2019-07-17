package com.tendebit.dungeonmaster.charactercreation3.race.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmastercore.view.adapter.SingleSelectAdapter
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel

class DndRaceSelectionAdapter(private val viewModel: SingleSelectViewModel<DndRace>?) : SingleSelectAdapter<DndRace, DndRaceViewHolder>(viewModel) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DndRaceViewHolder {
		return DndRaceViewHolder(LayoutInflater.from(parent.context), parent)
	}

	override fun onBindViewHolder(holder: DndRaceViewHolder, position: Int) {
		holder.populate(viewModel!!.children[position])
	}

}
