package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class CharacterProficiencySelectionState(parentStateChanges: Observable<CharacterCreationState>) {
    val proficiencyGroups = ArrayList<CharacterProficiencyGroupSelectionState>()
    private val stateSubject = BehaviorSubject.create<CharacterProficiencySelectionState>()
    private val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()
    private val disposables = CompositeDisposable()
    val changes = stateSubject as Observable<CharacterProficiencySelectionState>

    init {
        disposables.add(parentStateChanges
                .filter { it.selectedClass != null }
                .map { it.selectedClass!! }
                .distinctUntilChanged()
                .subscribe{
                    onNewClassSelected(it)
                })
    }

    fun isProficiencySelectableForGroup(proficiency: CharacterProficiencyDirectory, groupId: Int) : Boolean {
        val groupState = proficiencyGroups[groupId]
        return !(!groupState.selectedProficiencies.contains(proficiency) && selectedProficiencies.contains(proficiency))
                && (groupState.selectedProficiencies.contains(proficiency) || groupState.selectedProficiencies.size < groupState.proficiencyGroup.choiceCount)
    }

    fun isProficiencySelected(proficiency: CharacterProficiencyDirectory) : Boolean {
        return selectedProficiencies.contains(proficiency)
    }

    fun onProficiencySelected(proficiency: CharacterProficiencyDirectory, id: Int) {
        selectedProficiencies.add(proficiency)
        proficiencyGroups[id].selectedProficiencies.add(proficiency)
        notifyDataChanged()
    }

    fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory, groupId: Int) {
        selectedProficiencies.remove(proficiency)
        proficiencyGroups[groupId].selectedProficiencies.remove(proficiency)
        notifyDataChanged()
    }

    fun cancelAllSubscriptions() {
        disposables.dispose()
    }

    private fun onNewClassSelected(classInfo: CharacterClassInfo) {
        proficiencyGroups.clear()
        selectedProficiencies.clear()
        proficiencyGroups.addAll(Observable.fromIterable(classInfo.proficiencyChoices)
                .map { CharacterProficiencyGroupSelectionState(it) }
                .toList()
                .blockingGet())
        notifyDataChanged()
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}