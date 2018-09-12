package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection

import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyGroup

class ProficiencyGroupSelectionState(val proficiencyGroup: CharacterProficiencyGroup) {
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()
    fun remainingChoices() : Int {
        return proficiencyGroup.choiceCount - selectedProficiencies.size
    }

}