package com.tendebit.dungeonmaster.charactercreation3.proficiency.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup

interface DndProficiencyDataStore {

	suspend fun getProficiencyList(characterClass: DndCharacterClass, forceNetwork: Boolean = false): List<DndProficiencyGroup>

	fun restoreProficiencyList(characterClass: DndCharacterClass, proficiencyList: List<DndProficiencyGroup>)

}