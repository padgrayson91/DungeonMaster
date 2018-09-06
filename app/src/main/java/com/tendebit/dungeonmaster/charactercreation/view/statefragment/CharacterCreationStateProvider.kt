package com.tendebit.dungeonmaster.charactercreation.view.statefragment

import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import io.reactivex.Observable


interface CharacterCreationStateProvider {
    val stateChanges: Observable<CharacterCreationState>

    fun onProficiencySelected(selection: CharacterProficiencyDirectory)
    fun onPageSelected(selection: Int)
}