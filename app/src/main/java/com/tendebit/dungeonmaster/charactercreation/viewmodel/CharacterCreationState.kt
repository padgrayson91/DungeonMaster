package com.tendebit.dungeonmaster.charactercreation.viewmodel

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import java.util.*


class CharacterCreationState {
    var currentPage = 0
    val availablePages = LinkedList<CharacterCreationPageDescriptor>()
    var selectedClass: CharacterClassInfo? = null
    var selectedRace: CharacterRaceDirectory? = null
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()

    fun clearPagesStartingAt(index: Int) {
        if (index >= availablePages.size) return
        availablePages.subList(index, availablePages.size).clear()
        if (currentPage >= availablePages.size) {
            currentPage = availablePages.size
        }
    }

    fun addPage(pageDescriptor: CharacterCreationPageDescriptor) {
        availablePages.add(pageDescriptor)
    }
}