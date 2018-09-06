package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment

import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import io.reactivex.Observable

interface ProficiencySelectionStateProvider {
    val stateChanges: Observable<CharacterProficiencySelectionState>
    fun onProficiencySelected(proficiency: CharacterProficiencyDirectory, id: Int)
    fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory, id: Int)
    fun onProficienciesConfirmed()
}