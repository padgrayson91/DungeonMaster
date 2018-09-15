package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection

import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyGroup

/**
 * ViewModel for the state of a single group of proficiencies from which the user may make selections
 */
class ProficiencyGroupSelectionViewModel(val proficiencyGroup: CharacterProficiencyGroup) {
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()
    fun remainingChoices() : Int {
        return proficiencyGroup.choiceCount - selectedProficiencies.size
    }

}