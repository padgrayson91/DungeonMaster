package com.tendebit.dungeonmaster.charactercreation3.race.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityType
import com.tendebit.dungeonmaster.charactercreation3.race.DndDetailedRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

internal class DndRaceInfo {
	@SerializedName("name")
	lateinit var name: String
	@SerializedName("url")
	lateinit var url: String
	@SerializedName("ability_bonuses")
	lateinit var bonuses: Array<Int>

	override fun toString(): String {
		return "Entry for race $name can be found at $url"
	}

	fun toDetailedRace(): DndDetailedRace {
		val origin = DndRace(name, url)
		val bonusItems = bonuses.toList().mapIndexed { index, value -> DndAbilityBonus(DndAbilityType.sortedValues[index], value) }.toTypedArray()
		return DndDetailedRace(origin, bonusItems)
	}
}
