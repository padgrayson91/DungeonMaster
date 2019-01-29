package com.tendebit.dungeonmaster.charactercreation.model.requirement

import com.tendebit.dungeonmaster.charactercreation.model.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassManifest
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndClassRequirement(initialValue: CharacterClassDirectory?, val choices: List<CharacterClassDirectory>): SimpleRequirement<CharacterClassDirectory>(initialValue)

class DndClassOptionsRequirement(initialValue: CharacterClassManifest?): SimpleRequirement<CharacterClassManifest>(initialValue)

class DndRaceOptionsRequirement(initialValue: Iterable<CharacterRaceDirectory>?): SimpleRequirement<Iterable<CharacterRaceDirectory>>(initialValue)

class DndRaceRequirement(initialValue: CharacterRaceDirectory?, val choices: List<CharacterRaceDirectory>): SimpleRequirement<CharacterRaceDirectory>(initialValue)

class DndProficiencyOptionsRequirement(initialValue: List<DndProficiencyGroup>?): SimpleRequirement<List<DndProficiencyGroup>>(initialValue)

class DndProficiencyRequirement(initialValue: DndProficiency?, val fromGroup: DndProficiencyGroup): SimpleRequirement<DndProficiency>(initialValue) {

	override fun update(item: DndProficiency) {
		if (!fromGroup.availableOptions.contains(item)) {
			return
		}
		super.update(item)
	}

	override fun revoke() {
		// do not null the value so that we know which item was deselected
		internalStatus.onNext(Requirement.Status.NOT_FULFILLED)
		status = Requirement.Status.NOT_FULFILLED
	}
}