package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import io.reactivex.Observable

interface DndCharacterClassProvider {

	val state: ItemState<out DndCharacterClassSelection>

	val externalStateChanges: Observable<ItemState<out DndCharacterClassSelection>>
	val internalStateChanges: Observable<ItemState<out DndCharacterClassSelection>>

	fun refreshClassState()

	suspend fun start()

}
