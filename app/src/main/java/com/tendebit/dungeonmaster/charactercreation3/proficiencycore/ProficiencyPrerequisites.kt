package com.tendebit.dungeonmaster.charactercreation3.proficiencycore

import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import io.reactivex.Observable

interface ProficiencyPrerequisites {

	val sources: List<Observable<ItemState<out DndProficiencySource>>>
	val storage: DndProficiencyStorage
	val concurrency: Concurrency

	class Impl(override val concurrency: Concurrency,
			   override val sources: List<Observable<ItemState<out DndProficiencySource>>>,
			   override val storage: DndProficiencyStorage) : ProficiencyPrerequisites

}
