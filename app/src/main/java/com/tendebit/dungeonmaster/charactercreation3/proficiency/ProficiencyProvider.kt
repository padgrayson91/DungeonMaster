package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.core.model.ItemState
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope

interface ProficiencyProvider {

	val state: ItemState<out DndProficiencySelection>
	val internalStateChanges: Observable<ItemState<out DndProficiencySelection>>
	val externalStateChanges: Observable<ItemState<out DndProficiencySelection>>

	fun start(prerequisites: ProficiencyPrerequisites, scope: CoroutineScope)
	fun refreshProficiencyState()

}
