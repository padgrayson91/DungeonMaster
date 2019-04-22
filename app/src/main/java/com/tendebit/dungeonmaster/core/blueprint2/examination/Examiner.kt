package com.tendebit.dungeonmaster.core.blueprint2.examination

import com.tendebit.dungeonmaster.core.blueprint2.Delta

interface Examiner<StateType, SubState> {

	val substate: SubState

	fun performExamination(state: StateType): Delta<SubState>

}
