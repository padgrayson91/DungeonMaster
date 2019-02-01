package com.tendebit.dungeonmaster.charactercreation.model.examiner

interface Examiner<StateType> {

	fun examine(state: StateType): Examination<StateType>

}
