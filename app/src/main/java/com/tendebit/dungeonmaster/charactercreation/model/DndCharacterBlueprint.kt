package com.tendebit.dungeonmaster.charactercreation.model

import com.tendebit.dungeonmaster.charactercreation.model.examiner.CharacterClassExaminer
import com.tendebit.dungeonmaster.charactercreation.model.examiner.CharacterPrerequisiteExaminer
import com.tendebit.dungeonmaster.charactercreation.model.examiner.CharacterProficiencyExaminer
import com.tendebit.dungeonmaster.charactercreation.model.examiner.CharacterProficiencyOptionsExaminer
import com.tendebit.dungeonmaster.charactercreation.model.examiner.CharacterRaceExaminer
import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndCharacterBlueprint {

	private val internalRequirements = PublishSubject.create<List<Requirement<*>>>()
	val requirements = internalRequirements as Observable<List<Requirement<*>>>

	private val state = DndCharacterCreationState()
	private val stateChanges = PublishSubject.create<DndCharacterCreationState>()

	private val examiners = listOf(
			CharacterPrerequisiteExaminer(),
			CharacterClassExaminer(),
			CharacterRaceExaminer(),
			CharacterProficiencyOptionsExaminer(),
			CharacterProficiencyExaminer())

	init {
		subscribeExaminers()
		stateChanges.onNext(state)
	}

	fun cancel() {
		examiners.forEach { it.cancel() }
	}

	private fun subscribeExaminers() {
		examiners.forEach { examiner ->
			stateChanges.subscribe(examiner.inState)

			examiner.fulfillments.subscribe { fulfillmentList ->
				// FIXME: should find a way to merge so that stateChanges is only invoked after each active mapping has been applied
				fulfillmentList.forEach { fulfillment ->
					fulfillment.requirement.statusChanges.subscribe {
						if (fulfillment.applyToState(state)) {
							stateChanges.onNext(state)
						}
					}
				}
				internalRequirements.onNext(fulfillmentList.map { it.requirement }.toList())
			}
		}
	}

}
