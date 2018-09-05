package com.tendebit.dungeonmaster.charactercreation.raceselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.core.model.SelectionState

class CharacterRaceSelectionState : SelectionState<CharacterRaceDirectory, CharacterRaceDirectory> {
    override val options = ArrayList<CharacterRaceDirectory>()
    override var selection: CharacterRaceDirectory? = null

    override fun updateOptions(options: List<CharacterRaceDirectory>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun select(option: CharacterRaceDirectory) {
        this.selection = option
    }
}