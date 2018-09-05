package com.tendebit.dungeonmaster.charactercreation.viewmodel

class CharacterCreationPageDescriptor(val type: PageType, val indexInGroup: Int) {
    enum class PageType {
        CLASS_SELECTION,
        PROFICIENCY_SELECTION
    }
}