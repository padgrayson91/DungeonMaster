package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import io.reactivex.Observable

interface ProficiencyPrerequisites {

	val classSelections: Observable<ItemState<out DndCharacterClassSelection>>

	class Impl(override val classSelections: Observable<ItemState<out DndCharacterClassSelection>>) : ProficiencyPrerequisites

}
