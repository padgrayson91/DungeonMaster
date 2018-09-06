package com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.CharacterClassSelectionState
import io.reactivex.Observable

interface ClassSelectionStateProvider {
    val stateChanges: Observable<CharacterClassSelectionState>
    fun onClassSelected(selection: CharacterClassDirectory)
}