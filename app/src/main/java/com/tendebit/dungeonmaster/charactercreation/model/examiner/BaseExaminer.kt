package com.tendebit.dungeonmaster.charactercreation.model.examiner

import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.Fulfillment
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class BaseExaminer<StateType>: Examiner<StateType> {

	final override val fulfillments = BehaviorSubject.create<List<Fulfillment<*, StateType>>>()
	final override val inState = PublishSubject.create<StateType>()
	private val mainDisposable: Disposable

	init {
		mainDisposable = inState.subscribe {
			fulfillments.onNext(getFulfillmentsForState(it))
		}
	}

	final override fun cancel() {
		mainDisposable.dispose()
	}

	abstract fun getFulfillmentsForState(state: StateType): List<Fulfillment<*, StateType>>

}