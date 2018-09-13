package com.tendebit.dungeonmaster.charactercreation.viewpager

class CharacterCreationPageDescriptor(val type: PageType, val indexInGroup: Int = 0,
                                      val isLastPage: Boolean = false) {
    enum class PageType {
        CHARACTER_LIST,
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
                && other.isLastPage == isLastPage
    }

    override fun hashCode(): Int {
        var result = 12
        result = 31 * result + type.hashCode()
        result = 31 * result + indexInGroup
        result = 31 * result + if (isLastPage) 1 else 0
        return result
    }
}