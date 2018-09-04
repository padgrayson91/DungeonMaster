package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyGroup

class CharacterProficiencySelectionState {
    var proficiencyGroup: CharacterProficiencyGroup? = null
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()

}