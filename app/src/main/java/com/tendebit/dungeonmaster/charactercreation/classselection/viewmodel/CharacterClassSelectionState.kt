package com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassInfo
import io.reactivex.subjects.BehaviorSubject

class CharacterClassSelectionState {
    val characterClassOptions = ArrayList<CharacterClassDirectory>()
    var selectedClass: CharacterClassInfo? = null

    fun updateOptions(options: List<CharacterClassDirectory>) {
        characterClassOptions.clear()
        characterClassOptions.addAll(options)
    }

    fun selectClass(selection: CharacterClassInfo) {
        selectedClass = selection
    }


}