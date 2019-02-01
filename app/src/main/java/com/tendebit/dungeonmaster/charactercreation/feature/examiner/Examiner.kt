package com.tendebit.dungeonmaster.charactercreation.feature.examiner

interface Examiner<StateType> {

	fun examine(state: StateType): Examination<StateType>

}
