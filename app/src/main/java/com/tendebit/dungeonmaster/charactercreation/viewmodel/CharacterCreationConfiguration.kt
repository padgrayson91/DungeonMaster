package com.tendebit.dungeonmaster.charactercreation.viewmodel

import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory

class CharacterCreationConfiguration {

    val characterClassOptions = ArrayList<CharacterClassDirectory>()

    fun updateClassOptions(options: List<CharacterClassDirectory>) {
        characterClassOptions.clear()
        characterClassOptions.addAll(options)
    }
}