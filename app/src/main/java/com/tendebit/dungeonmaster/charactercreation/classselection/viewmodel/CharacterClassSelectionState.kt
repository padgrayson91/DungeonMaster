package com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.core.model.SelectionState

class CharacterClassSelectionState : SelectionState<CharacterClassDirectory, CharacterClassInfo> {
    override val options = ArrayList<CharacterClassDirectory>()
    override var selection: CharacterClassInfo? = null

    override fun updateOptions(options: List<CharacterClassDirectory>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun select(option: CharacterClassInfo) {
        this.selection = option
    }


}