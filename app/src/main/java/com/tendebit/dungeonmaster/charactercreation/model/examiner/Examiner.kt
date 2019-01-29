package com.tendebit.dungeonmaster.charactercreation.model.examiner

import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.Fulfillment
import io.reactivex.Observable
import io.reactivex.Observer

interface Examiner<StateType> {

	val fulfillments: Observable<List<Fulfillment<*, StateType>>>
	val inState: Observer<StateType>

	fun cancel()

}
