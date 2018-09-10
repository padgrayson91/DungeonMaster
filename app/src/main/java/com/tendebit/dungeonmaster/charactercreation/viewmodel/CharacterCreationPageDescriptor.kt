package com.tendebit.dungeonmaster.charactercreation.viewmodel

class CharacterCreationPageDescriptor(val type: PageType, val indexInGroup: Int) {
    enum class PageType {
        RACE_SELECTION,
        CLASS_SELECTION,
        PROFICIENCY_SELECTION,
        CUSTOM_INFO,
        CONFIRMATION
    }

    override fun equals(other: Any?): Boolean {
        return other is CharacterCreationPageDescriptor
                && other.type == type
                && other.indexInGroup == indexInGroup
    }

    override fun hashCode(): Int {
        var result = 12
        result = 31 * result + type.hashCode()
        result = 31 * result + indexInGroup
        return result
    }
}