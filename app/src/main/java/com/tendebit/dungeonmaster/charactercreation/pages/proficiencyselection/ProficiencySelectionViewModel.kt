package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * ViewModel for the overall state of proficiency selection.  This is needed because in some cases multiple
 * proficiency groups may contain the same option, and if it is selected for one group the other group needs
 * to be able to access that information
 */
class ProficiencySelectionViewModel(classInfo: CharacterClassInfo) {
    private val proficiencyGroups = ArrayList<ProficiencyGroupSelectionViewModel>()
    private val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    val selectionChanges = BehaviorSubject.create<Pair<Collection<CharacterProficiencyDirectory>, List<ProficiencyGroupSelectionViewModel>>>()
    val completionChanges = BehaviorSubject.create<Boolean>()

    init {
        proficiencyGroups.addAll(Observable.fromIterable(classInfo.proficiencyChoices)
                .map { ProficiencyGroupSelectionViewModel(it) }
                .toList()
                .blockingGet())
        selectionChanges.onNext(Pair(selectedProficiencies, proficiencyGroups))
        completionChanges.onNext(false)
    }

    fun isProficiencySelectableForGroup(proficiency: CharacterProficiencyDirectory, groupViewModel: ProficiencyGroupSelectionViewModel, selections: Collection<CharacterProficiencyDirectory>) : Boolean {
        return !(!groupViewModel.selectedProficiencies.contains(proficiency) && selections.contains(proficiency))
                && (groupViewModel.selectedProficiencies.contains(proficiency) || groupViewModel.selectedProficiencies.size < groupViewModel.proficiencyGroup.choiceCount)
    }

    fun onProficiencySelected(proficiency: CharacterProficiencyDirectory, id: Int) {
        selectedProficiencies.add(proficiency)
        proficiencyGroups[id].selectedProficiencies.add(proficiency)
        selectionChanges.onNext(Pair(selectedProficiencies, proficiencyGroups))
        completionChanges.onNext(proficiencyGroups.asSequence().map { it.remainingChoices() }.sum() == 0)
    }

    fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory, groupId: Int) {
        selectedProficiencies.remove(proficiency)
        proficiencyGroups[groupId].selectedProficiencies.remove(proficiency)
        selectionChanges.onNext(Pair(selectedProficiencies, proficiencyGroups))
        completionChanges.onNext(proficiencyGroups.asSequence().map { it.remainingChoices() }.sum() == 0)
    }
}