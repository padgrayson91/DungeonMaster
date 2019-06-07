package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.core.model.Selection
import io.reactivex.Observable

interface ProficiencyPrerequisites {

	val classSelections: Observable<ItemState<out Selection<DndCharacterClass>>>

	class Impl(override val classSelections: Observable<ItemState<out Selection<DndCharacterClass>>>) : ProficiencyPrerequisites

}
