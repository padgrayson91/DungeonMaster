package com.tendebit.dungeonmaster.charactercreation.feature.requirement

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace

class DndClassRequirement(initialValue: DndClass?, val choices: List<DndClass>): SimpleRequirement<DndClass>(initialValue)

class DndClassOptionsRequirement(initialValue: List<DndClass>): ListRequirement<DndClass>(initialValue)

class DndRaceOptionsRequirement(initialValue: List<DndRace>): ListRequirement<DndRace>(initialValue)

class DndRaceRequirement(initialValue: DndRace?, val choices: List<DndRace>): SimpleRequirement<DndRace>(initialValue)

class DndProficiencyOptionsRequirement(initialValue: List<DndProficiencyGroup>): SimpleRequirement<List<DndProficiencyGroup>>(initialValue)

class DndProficiencyRequirement(initialValue: DndProficiency?, val fromGroup: DndProficiencyGroup): SimpleRequirement<DndProficiency>(initialValue) {

	override fun isItemValid(item: DndProficiency): Boolean = super.isItemValid(item) && fromGroup.availableOptions.contains(item)

	/**
	 * [onRevoke] for elements of this type is a NOOP. Data about the revoked [DndProficiency] is retained so that observers can easily determine
	 * which item was deselected
	 */
	override fun onRevoke() {}

}
