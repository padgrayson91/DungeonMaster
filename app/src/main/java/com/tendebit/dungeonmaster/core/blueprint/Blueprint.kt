package com.tendebit.dungeonmaster.core.blueprint

import com.tendebit.dungeonmaster.core.blueprint.examination.Examination
import com.tendebit.dungeonmaster.core.blueprint.examination.Examiner
import com.tendebit.dungeonmaster.core.blueprint.fulfillment.Fulfillment
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.LinkedList

class Blueprint<StateType>(private val examiners: List<Examiner<StateType>>, initialState: StateType) {

	private val internalRequirements = BehaviorSubject.create<List<Delta<Requirement<*>>>>()
	val requirements = internalRequirements as Observable<List<Delta<Requirement<*>>>>
	private val examinations = HashMap<Examiner<StateType>, Examination<StateType>>()
	private val subscriptions = HashMap<Examiner<StateType>, MutableList<Disposable>>()

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
			val disposables = subscriptions[examiner] ?: LinkedList()
			val index = item.index
			var disposableIndex = 0
			val previousExamination = examinations[examiner]
			val examination = examiner.examineWithDelta(state, previousExamination)
			examinations[examiner] = examination
			changeLoop@ for (change in examination.changes) {
				val fulfillment = change.item
				requirementChangesForState.add(Delta(change.type, fulfillment?.requirement))
				when (change.type) {
					Delta.Type.INSERTION -> {
						if (fulfillment == null) throw IllegalStateException("Inserted a null ${Fulfillment<*, *>::javaClass.name}")
						if (disposableIndex < disposables.size) {
							disposables.add(disposableIndex, subscribeToFulfillmentAtIndex(state, fulfillment, index))
						} else {
							disposables.add(subscribeToFulfillmentAtIndex(state, fulfillment, index))
						}
						disposableIndex++
					}
					Delta.Type.REMOVAL -> {
						disposables.removeAt(disposableIndex).dispose()
					}
					Delta.Type.UNCHANGED -> {
						disposableIndex++
					}
					Delta.Type.UPDATE -> {
						val temp = disposableIndex
						if (temp >= disposables.size) throw IllegalStateException("Attempting to perform an ")
						disposables[temp].dispose()
						disposableIndex++
						if (fulfillment == null) throw IllegalStateException("Updated to a null ${Fulfillment<*, *>::javaClass.name}")
						disposables[temp] = subscribeToFulfillmentAtIndex(state, fulfillment, index)
					}
				}
			}
			subscriptions[examiner] = disposables
			if (examination.shouldHalt) {
				break
			}
		}
		internalRequirements.onNext(requirementChangesForState)
	}

	private fun subscribeToFulfillmentAtIndex(state: StateType, fulfillment: Fulfillment<*, StateType>, index: Int): Disposable {
		return fulfillment.requirement.statusChanges.subscribe {
			if (fulfillment.applyToState(state)) {
				examineState(state, index)
			}
		}
	}

}
