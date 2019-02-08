package com.tendebit.dungeonmaster.charactercreation.feature

import com.tendebit.dungeonmaster.charactercreation.feature.examiner.CharacterClassExaminer
import com.tendebit.dungeonmaster.charactercreation.feature.examiner.CharacterPrerequisiteExaminer
import com.tendebit.dungeonmaster.charactercreation.feature.examiner.CharacterProficiencyExaminer
import com.tendebit.dungeonmaster.charactercreation.feature.examiner.CharacterProficiencyOptionsExaminer
import com.tendebit.dungeonmaster.charactercreation.feature.examiner.CharacterRaceExaminer
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class DndCharacterBlueprint {

	private val internalRequirements = BehaviorSubject.create<List<Requirement<*>>>()
	val requirements = internalRequirements as Observable<List<Requirement<*>>>

	private val state = DndCharacterCreationState()
	private val stateChanges = PublishSubject.create<DndCharacterCreationState>()
	private var mainDisposable: Disposable? = null
	private var stateDisposable = CompositeDisposable()

	private val examiners = listOf(
			CharacterPrerequisiteExaminer(),
			CharacterRaceExaminer(),
			CharacterClassExaminer(),
			CharacterProficiencyOptionsExaminer(),
			CharacterProficiencyExaminer())

	init {
		subscribeExaminers()
		stateChanges.onNext(state)
	}

	fun destroy() {
		internalRequirements.onComplete()
		mainDisposable?.dispose()
		stateDisposable.dispose()
	}

	fun clear() {
		state.clear()
		stateChanges.onNext(state)
	}

	private fun subscribeExaminers() {
		mainDisposable = stateChanges.subscribe {
			stateDisposable.dispose()
			stateDisposable = CompositeDisposable()

			val allRequirementsForState = ArrayList<Requirement<*>>()
			for (examiner in examiners) {
				val examination = examiner.examine(it)
				for (fulfillment in examination.fulfillmentList) {
					stateDisposable.add(fulfillment.requirement.statusChanges.subscribe {
						// FIXME: should find a way to merge so that stateChanges is only invoked after each active fulfillment has been applied
						if (fulfillment.applyToState(state)) {
							stateChanges.onNext(state)
						}
					})
					allRequirementsForState.add(fulfillment.requirement)
				}

				// This examiner thinks no other examiners will work
				if (examination.shouldHalt) break
			}

			internalRequirements.onNext(allRequirementsForState)
		}
	}

}
