package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmaster.BuildConfig
import com.tendebit.dungeonmastercore.debug.Logger
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed

const val TAG = "ABL"
internal val logger = Logger(com.tendebit.dungeonmaster.charactercreation3.TAG, TAG, debug = BuildConfig.DEBUG)
val EMPTY_BONUS_ARRAY = arrayOf(
		DndAbilityBonus(DndAbilityType.STR, 0),
		DndAbilityBonus(DndAbilityType.DEX, 0),
		DndAbilityBonus(DndAbilityType.CON, 0),
		DndAbilityBonus(DndAbilityType.INT, 0),
		DndAbilityBonus(DndAbilityType.WIS, 0),
		DndAbilityBonus(DndAbilityType.CHA, 0))
val EMPTY_ABILITY_SLOTS = arrayOf<ItemState<out DndAbilitySlot>>(
		Normal(DndAbilitySlot(Removed, EMPTY_BONUS_ARRAY[0])),
		Normal(DndAbilitySlot(Removed, EMPTY_BONUS_ARRAY[1])),
		Normal(DndAbilitySlot(Removed, EMPTY_BONUS_ARRAY[2])),
		Normal(DndAbilitySlot(Removed, EMPTY_BONUS_ARRAY[3])),
		Normal(DndAbilitySlot(Removed, EMPTY_BONUS_ARRAY[4])),
		Normal(DndAbilitySlot(Removed, EMPTY_BONUS_ARRAY[5])))