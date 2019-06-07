package com.tendebit.dungeonmaster.charactercreation3.race

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.Selection
import io.reactivex.Observable

interface DndRaceProvider {

	val state: ItemState<out Selection<DndRace>>

	val externalStateChanges: Observable<ItemState<out Selection<DndRace>>>
	val internalStateChanges: Observable<ItemState<out Selection<DndRace>>>

	fun refreshClassState()

	fun start(concurrency: Concurrency)

}
