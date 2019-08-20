package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySlotViewModel

class DndAbilitySlotViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_ability_slot, parent, false)) {

	private val abilityName = itemView.findViewById<TextView>(R.id.ability_name)
	private val rollValueText = itemView.findViewById<TextView>(R.id.raw_value)
	private val indicatorImage = itemView.findViewById<View>(R.id.indicator_icon)
	private val bonusText = itemView.findViewById<TextView>(R.id.bonus)
	private val modifierText = itemView.findViewById<TextView>(R.id.modifier)

	fun populate(abilitySlotViewModel: DndAbilitySlotViewModel) {
		itemView.setOnClickListener { abilitySlotViewModel.onClick() }
		abilityName.text = itemView.resources.getString(abilitySlotViewModel.abilityNameTextRes)
		rollValueText.text = abilitySlotViewModel.rawScoreText
		indicatorImage.visibility = if (abilitySlotViewModel.showIndicator) View.VISIBLE else View.GONE
		bonusText.text = abilitySlotViewModel.bonusText
		modifierText.text = abilitySlotViewModel.modifierText
	}

}
