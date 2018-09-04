package com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.statefragment

import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.CharacterClassSelectionState
import io.reactivex.Observable

interface ClassSelectionStateProvider {
    val stateChanges: Observable<CharacterClassSelectionState>
    fun onClassSelected(selection: CharacterClassDirectory)
}