package com.tendebit.dungeonmaster.charactercreation3.characterclass.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmastercore.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes

class DndClassViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_generic_selection, parent, false)) {
	private val classNameText = itemView.findViewById<TextView>(R.id.primary_item_text)

	fun populate(viewModel: SelectableViewModel<DndCharacterClass>) {
		itemView.setOnClickListener {
			logger.writeDebug("Clicked $it")
			viewModel.onClick()
		}
		classNameText.text = viewModel.text
		classNameText.isSelected = viewModel.textType == TextTypes.SELECTED
	}

}
