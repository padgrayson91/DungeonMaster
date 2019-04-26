package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import io.reactivex.Observable

interface ProficiencyPrerequisites {

	val classSelections: Observable<DndCharacterClass>

}