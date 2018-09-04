package com.tendebit.dungeonmaster.charactercreation.viewmodel

import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import java.util.*


class CharacterCreationState {
    var currentPage = 0
    val availablePages = LinkedList<CharacterCreationPageDescriptor>()
    var selectedClass: CharacterClassInfo? = null
    val selectedProficiencies = HashSet<CharacterProficiencyDirectory>()

    fun clearPages(index: Int) {
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