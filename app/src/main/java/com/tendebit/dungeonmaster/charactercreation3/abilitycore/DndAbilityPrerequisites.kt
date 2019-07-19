package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import io.reactivex.Observable

interface DndAbilityPrerequisites {

	val sources: List<Observable<ItemState<out DndAbilitySource>>>
	val concurrency: Concurrency

	class Impl(override val concurrency: Concurrency, override val sources: List<Observable<ItemState<out DndAbilitySource>>>) : DndAbilityPrerequisites

}
