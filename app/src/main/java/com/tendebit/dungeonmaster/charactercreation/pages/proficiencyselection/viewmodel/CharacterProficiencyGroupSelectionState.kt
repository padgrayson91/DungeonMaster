package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyGroup

class CharacterProficiencyGroupSelectionState(val proficiencyGroup: CharacterProficiencyGroup) {
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()
    fun remainingChoices() : Int {
        return proficiencyGroup.choiceCount - selectedProficiencies.size
    }

}