package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.logger
import com.tendebit.dungeonmastercore.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes

class DndAbilityDiceRollViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_ability_roll, parent, false)) {

	private val rollText = itemView.findViewById<TextView>(R.id.text)

	fun populate(viewModel: SelectableViewModel<Int>) {
		itemView.setOnClickListener {
			logger.writeDebug("Clicked $it")
			viewModel.onClick()
		}
		rollText.text = viewModel.text
		rollText.setTextColor(when (viewModel.textType) {
			// FIXME: should use selected/normal view state and styles to set color rather than calling setTextColor directly
			TextTypes.SELECTED -> R.attr.itemTextColor
			TextTypes.NORMAL -> ContextCompat.getColor(itemView.context, R.color.colorAccent)
		})

		// TODO: show highlight
	}

}
