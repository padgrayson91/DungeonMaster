package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.core.model.ItemState
import io.reactivex.Observable

interface ProficiencyProvider {

	val state: ItemState<out DndProficiencySelection>
	val internalStateChanges: Observable<ItemState<out DndProficiencySelection>>
	val externalStateChanges: Observable<ItemState<out DndProficiencySelection>>

	fun refreshProficiencyState()

}
