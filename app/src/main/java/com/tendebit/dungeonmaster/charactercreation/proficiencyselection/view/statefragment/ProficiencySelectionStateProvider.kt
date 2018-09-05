package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.view.statefragment

import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.CharacterProficiencyGroupSelectionState
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import io.reactivex.Observable

interface ProficiencySelectionStateProvider {
    val stateChanges: Observable<CharacterProficiencySelectionState>
    fun onProficiencySelected(proficiency: CharacterProficiencyDirectory, id: Int)
    fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory, id: Int)
    fun onProficienciesConfirmed()
}