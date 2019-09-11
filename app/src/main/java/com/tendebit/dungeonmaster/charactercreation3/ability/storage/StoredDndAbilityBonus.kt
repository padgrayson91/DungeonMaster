package com.tendebit.dungeonmaster.charactercreation3.ability.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityType

@Entity(tableName = "ability_bonuses")
data class StoredDndAbilityBonus(
		@PrimaryKey val id: CharSequence,
		@ColumnInfo(name = "str") val str: Int,
		@ColumnInfo(name = "dex") val dex: Int,
		@ColumnInfo(name = "str") val con: Int,
		@ColumnInfo(name = "dex") val intel: Int,
		@ColumnInfo(name = "str") val wis: Int,
		@ColumnInfo(name = "dex") val cha: Int) {

	companion object {
		fun fromAbilityBonuses(bonuses: Array<DndAbilityBonus>, sourceId: CharSequence): StoredDndAbilityBonus {
			return StoredDndAbilityBonus(
					sourceId,
					bonuses.find { it.type == DndAbilityType.STR }?.value ?: 0,
					bonuses.find { it.type == DndAbilityType.DEX }?.value ?: 0,
					bonuses.find { it.type == DndAbilityType.CON }?.value ?: 0,
					bonuses.find { it.type == DndAbilityType.INT }?.value ?: 0,
					bonuses.find { it.type == DndAbilityType.WIS }?.value ?: 0,
					bonuses.find { it.type == DndAbilityType.CHA }?.value ?: 0)
		}
	}

	fun toAbilityBonusArray(): Array<DndAbilityBonus> {
		return arrayOf(
				DndAbilityBonus(DndAbilityType.STR, str),
				DndAbilityBonus(DndAbilityType.DEX, dex),
				DndAbilityBonus(DndAbilityType.CON, con),
				DndAbilityBonus(DndAbilityType.INT, intel),
				DndAbilityBonus(DndAbilityType.WIS, wis),
				DndAbilityBonus(DndAbilityType.CHA, cha))
	}

}
