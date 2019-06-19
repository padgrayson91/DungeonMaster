package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.model.Selection
import io.reactivex.Observable

interface ProficiencyPrerequisites {

	val classSelections: Observable<ItemState<out Selection<DndCharacterClass>>>
	val storage: DndProficiencyStorage

	class Impl(override val classSelections: Observable<ItemState<out Selection<DndCharacterClass>>>, override val storage: DndProficiencyStorage) : ProficiencyPrerequisites

}
