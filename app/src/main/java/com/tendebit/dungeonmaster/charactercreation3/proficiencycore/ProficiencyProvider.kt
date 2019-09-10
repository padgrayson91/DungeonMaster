package com.tendebit.dungeonmaster.charactercreation3.proficiencycore

import com.tendebit.dungeonmastercore.model.state.ItemState
import io.reactivex.Observable

interface ProficiencyProvider {

	val state: ItemState<out DndProficiencySelection>
	val internalStateChanges: Observable<ItemState<out DndProficiencySelection>>
	val externalStateChanges: Observable<ItemState<out DndProficiencySelection>>

	fun refreshProficiencyState()

}
