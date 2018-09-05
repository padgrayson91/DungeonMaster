package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyGroup

class CharacterProficiencyGroupSelectionState(val proficiencyGroup: CharacterProficiencyGroup) {
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()
    fun remainingChoices() : Int {
        return proficiencyGroup.choiceCount - selectedProficiencies.size
    }

}