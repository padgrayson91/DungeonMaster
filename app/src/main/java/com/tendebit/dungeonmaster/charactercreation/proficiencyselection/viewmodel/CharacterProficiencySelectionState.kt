package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyGroup
import io.reactivex.subjects.BehaviorSubject

class CharacterProficiencySelectionState {
    var proficiencyGroup: CharacterProficiencyGroup? = null
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()
    val selectionChanges = BehaviorSubject.create<Collection<CharacterProficiencyDirectory>>()

    fun selectProficiency(proficiency: CharacterProficiencyDirectory) {
        selectedProficiencies.add(proficiency)
        selectionChanges.onNext(selectedProficiencies)
    }

    fun deselectProficiency(proficiency: CharacterProficiencyDirectory) {
        selectedProficiencies.remove(proficiency)
        selectionChanges.onNext(selectedProficiencies)
    }

}