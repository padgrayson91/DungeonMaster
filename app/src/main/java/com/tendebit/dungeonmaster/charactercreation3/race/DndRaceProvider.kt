package com.tendebit.dungeonmaster.charactercreation3.race

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import io.reactivex.Observable

interface DndRaceProvider {

	val state: ItemState<out DndRaceSelection>

	val externalStateChanges: Observable<ItemState<out DndRaceSelection>>
	val internalStateChanges: Observable<ItemState<out DndRaceSelection>>

	fun refreshClassState()

	fun start(concurrency: Concurrency)

}
