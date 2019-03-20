package com.tendebit.dungeonmaster.core.blueprint

import com.tendebit.dungeonmaster.core.blueprint.examination.Examination
import com.tendebit.dungeonmaster.core.blueprint.examination.Examiner
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.LinkedList

class Blueprint<StateType>(private val examiners: List<Examiner<StateType>>, initialState: StateType) {

	private val internalRequirements = BehaviorSubject.create<List<Delta<Requirement<*>>>>()
	val requirements = internalRequirements as Observable<List<Delta<Requirement<*>>>>
	private val examinations = HashMap<Examiner<StateType>, Examination<StateType>>()
	private val subscriptions = HashMap<Examiner<StateType>, Disposable>()

	init {
		examineState(initialState)
	}

	private fun examineState(state: StateType, startingWithIndex: Int = 0) {
		val requirementChangesForState = LinkedList<Delta<Requirement<*>>>()
		// Examiners prior to the start index are left unchanged
		for (examiner in examiners.subList(0, startingWithIndex)) {
			examinations[examiner]?.fulfillmentList
					?.map { Delta(Delta.Type.UNCHANGED, it.requirement) }
					?.forEach { requirementChangesForState.add(it) }
		}

		for (item in examiners.subList(startingWithIndex, examiners.size).withIndex()) {
			val examiner = item.value
			subscriptions[examiner]?.dispose()
			val disposable = CompositeDisposable()
			subscriptions[examiner] = disposable
			val index = item.index
			val examination = examiner.examineWithDelta(state, examinations[examiner])
			for (change in examination.changes) {
				val fulfillment = change.item ?: continue
				disposable.add(fulfillment.requirement.statusChanges.subscribe {
					if (fulfillment.applyToState(state)) {
						examineState(state, index)
					}
				})
			}
			examination.changes
					.map { Delta(it.type, it.item?.requirement) }
					.forEach { requirementChangesForState.add(it) }
		}
		internalRequirements.onNext(requirementChangesForState)
	}

}
