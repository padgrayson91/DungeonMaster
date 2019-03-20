package com.tendebit.dungeonmaster.core.blueprint.examination

import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.fulfillment.Fulfillment

interface Examiner<StateType> {

	fun examine(state: StateType): Examination<StateType>

	fun examineWithDelta(state: StateType, previous: Examination<StateType>?): DeltaExamination<StateType> {
		val updated = examine(state)
		if (previous == null) return DeltaExamination.Impl(updated)
		if (previous.isEmpty() && updated.isEmpty()) return DeltaExamination.Impl(previous)
		if (previous.isEmpty()) return DeltaExamination.Impl(updated)
		if (previous.isNotEmpty() && updated.isEmpty()) return DeltaExamination.Impl(previous.fulfillmentList.map { Delta(Delta.Type.REMOVAL, it) }, true)

		val deltas = ArrayList<Delta<Fulfillment<*, StateType>>>()
		for (item in updated.withIndex()) {
			val fulfillment = item.value
			when {
				item.index >= previous.size -> deltas.add(Delta(Delta.Type.INSERTION, fulfillment))
				fulfillment.requirement == previous[item.index].requirement -> deltas.add(Delta(Delta.Type.UNCHANGED, fulfillment))
				else -> deltas.add(Delta(Delta.Type.UPDATE, fulfillment))
			}
		}

		if (previous.size > updated.size) {
			val removals = Array(previous.size - updated.size) { Delta(Delta.Type.REMOVAL, previous[updated.size + it]) }
			deltas.addAll(removals)
		}

		return DeltaExamination.Impl(deltas, updated.shouldHalt)
	}

}
