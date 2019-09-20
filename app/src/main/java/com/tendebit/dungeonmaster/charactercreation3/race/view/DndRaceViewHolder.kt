package com.tendebit.dungeonmaster.charactercreation3.race.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmastercore.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes

class DndRaceViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_generic_selection, parent, false)) {
	private val raceNameText = itemView.findViewById<TextView>(R.id.primary_item_text)

	fun populate(viewModel: SelectableViewModel<DndRace>) {
		itemView.setOnClickListener { viewModel.onClick() }
		raceNameText.text = viewModel.text
		raceNameText.isSelected = viewModel.textType == TextTypes.SELECTED
	}

}