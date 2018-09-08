package com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.core.model.NetworkUIState
import com.tendebit.dungeonmaster.core.model.SelectionState

class CharacterClassSelectionState : SelectionState<CharacterClassDirectory, CharacterClassInfo>, NetworkUIState {
    override val options = ArrayList<CharacterClassDirectory>()
    override var selection: CharacterClassInfo? = null
    override var activeNetworkCalls = 0

    override fun updateOptions(options: List<CharacterClassDirectory>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun select(option: CharacterClassInfo) {
        this.selection = option
    }

}