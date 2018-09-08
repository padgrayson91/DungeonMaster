package com.tendebit.dungeonmaster.charactercreation.viewmodel

class CharacterCreationPageDescriptor(val type: PageType, val indexInGroup: Int) {
    enum class PageType {
        RACE_SELECTION,
        CLASS_SELECTION,
        PROFICIENCY_SELECTION,
        CONFIRMATION
    }
}