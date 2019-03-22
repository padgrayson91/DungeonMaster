package com.tendebit.dungeonmaster.core.blueprint.examination

import com.tendebit.dungeonmaster.core.blueprint.Delta

interface Examiner<StateType> {

	fun examine(state: StateType): Examination<StateType>

	fun examineWithDelta(state: StateType, previous: Examination<StateType>?): DeltaExamination<StateType> {
		val updated = examine(state)
		if (previous == null) return DeltaExamination.Impl(updated)
		if (previous.isEmpty() && updated.isEmpty()) return DeltaExamination.Impl(previous)
		if (previous.isEmpty()) return DeltaExamination.Impl(updated)
		if (previous.isNotEmpty() && updated.isEmpty()) return DeltaExamination.Impl(previous.fulfillmentList.map { Delta(Delta.Type.REMOVAL, it) }, true)

		val deltas = Delta.from(previous, updated)

		return DeltaExamination.Impl(deltas, updated.shouldHalt)
	}

}
