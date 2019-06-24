package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Selection
import io.reactivex.Observable

interface ProficiencyPrerequisites {

	val classSelections: Observable<ItemState<out Selection<DndCharacterClass>>>
	val raceSelections: Observable<ItemState<out Selection<DndRace>>>
	val storage: DndProficiencyStorage
	val concurrency: Concurrency

	class Impl(override val concurrency: Concurrency,
			   override val classSelections: Observable<ItemState<out Selection<DndCharacterClass>>>,
			   override val raceSelections: Observable<ItemState<out Selection<DndRace>>>,
			   override val storage: DndProficiencyStorage) : ProficiencyPrerequisites

}
