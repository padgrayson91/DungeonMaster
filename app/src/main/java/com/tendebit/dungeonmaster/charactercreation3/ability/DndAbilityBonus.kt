package com.tendebit.dungeonmaster.charactercreation3.ability

import java.io.Serializable

data class DndAbilityBonus(val type: DndAbilityType, val value: Int = 0) : Serializable