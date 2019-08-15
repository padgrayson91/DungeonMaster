package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import androidx.annotation.StringRes
import com.tendebit.dungeonmaster.R

enum class DndAbilityType(@StringRes val nameResId: Int) {

	STR(R.string.ability_str),
	DEX(R.string.ability_dex),
	CON(R.string.ability_con),
	INT(R.string.ability_int),
	WIS(R.string.ability_wis),
	CHA(R.string.ability_cha);

	companion object {
		val sortedValues = arrayOf(STR, DEX, CON, INT, WIS, CHA)
	}

}
