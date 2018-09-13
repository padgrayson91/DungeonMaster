package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

class ProficiencySelectionState {
    val proficiencyGroups = ArrayList<ProficiencyGroupSelectionState>()
    private val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    val selectionChanges = BehaviorSubject.create<Pair<Collection<CharacterProficiencyDirectory>, List<ProficiencyGroupSelectionState>>>()
    val completionChanges = BehaviorSubject.create<Boolean>()

    fun isProficiencySelectableForGroup(proficiency: CharacterProficiencyDirectory, groupState: ProficiencyGroupSelectionState, selections: Collection<CharacterProficiencyDirectory>) : Boolean {
        return !(!groupState.selectedProficiencies.contains(proficiency) && selections.contains(proficiency))
                && (groupState.selectedProficiencies.contains(proficiency) || groupState.selectedProficiencies.size < groupState.proficiencyGroup.choiceCount)
    }

    fun onProficiencySelected(proficiency: CharacterProficiencyDirectory, id: Int) {
        selectedProficiencies.add(proficiency)
        proficiencyGroups[id].selectedProficiencies.add(proficiency)
        selectionChanges.onNext(Pair(selectedProficiencies, proficiencyGroups))
        completionChanges.onNext(proficiencyGroups.map { it.remainingChoices() }.sum() == 0)
    }

    fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory, groupId: Int) {
        selectedProficiencies.remove(proficiency)
        proficiencyGroups[groupId].selectedProficiencies.remove(proficiency)
        selectionChanges.onNext(Pair(selectedProficiencies, proficiencyGroups))
        completionChanges.onNext(proficiencyGroups.map { it.remainingChoices() }.sum() == 0)
    }

    fun onNewClassSelected(classInfo: CharacterClassInfo) {
        proficiencyGroups.clear()
        selectedProficiencies.clear()
        proficiencyGroups.addAll(Observable.fromIterable(classInfo.proficiencyChoices)
                .map { ProficiencyGroupSelectionState(it) }
                .toList()
                .blockingGet())
        selectionChanges.onNext(Pair(selectedProficiencies, proficiencyGroups))
        completionChanges.onNext(false)
    }
}