package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.core.model.NetworkUIState
import com.tendebit.dungeonmaster.core.model.SelectionState

class CharacterRaceSelectionState : SelectionState<CharacterRaceDirectory, CharacterRaceDirectory>, NetworkUIState {
    override val options = ArrayList<CharacterRaceDirectory>()
    override var selection: CharacterRaceDirectory? = null
    override var activeNetworkCalls = 0

    override fun updateOptions(options: List<CharacterRaceDirectory>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun select(option: CharacterRaceDirectory) {
        this.selection = option
    }
}