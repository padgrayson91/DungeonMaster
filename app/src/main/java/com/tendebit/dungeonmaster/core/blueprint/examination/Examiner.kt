package com.tendebit.dungeonmaster.core.blueprint.examination

interface Examiner<StateType> {

	fun examine(state: StateType): Examination<StateType>

}
