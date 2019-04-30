package com.tendebit.dungeonmaster.charactercreation3

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndClasses
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencies
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyPrerequisites

class CharacterCreation {

	val character = DndCharacter()
	val classes = DndClasses()
	val proficiencies = DndProficiencies(ProficiencyPrerequisites.Impl(classes.allChanges))

}
