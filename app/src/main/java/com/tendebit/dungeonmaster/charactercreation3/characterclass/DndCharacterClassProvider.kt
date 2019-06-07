package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.Selection
import io.reactivex.Observable

interface DndCharacterClassProvider {

	val state: ItemState<out Selection<DndCharacterClass>>

	val externalStateChanges: Observable<ItemState<out Selection<DndCharacterClass>>>
	val internalStateChanges: Observable<ItemState<out Selection<DndCharacterClass>>>

	fun refreshClassState()

	fun start(concurrency: Concurrency)

}
