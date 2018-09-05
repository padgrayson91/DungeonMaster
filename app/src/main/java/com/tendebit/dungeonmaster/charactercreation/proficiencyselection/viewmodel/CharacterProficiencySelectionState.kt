package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel

import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory

class CharacterProficiencySelectionState {
    val proficiencyGroups = ArrayList<CharacterProficiencyGroupSelectionState>()
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()

    fun isProficiencySelectableForGroup(proficiency: CharacterProficiencyDirectory, groupId: Int) : Boolean {
        val groupState = proficiencyGroups[groupId]
        return !(!groupState.selectedProficiencies.contains(proficiency) && selectedProficiencies.contains(proficiency))
                && (groupState.selectedProficiencies.contains(proficiency) || groupState.selectedProficiencies.size < groupState.proficiencyGroup.choiceCount)
    }

    fun isProficiencySelected(proficiency: CharacterProficiencyDirectory) : Boolean {
        return selectedProficiencies.contains(proficiency)
    }
}