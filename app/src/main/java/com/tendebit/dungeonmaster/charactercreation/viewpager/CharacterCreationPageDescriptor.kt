package com.tendebit.dungeonmaster.charactercreation.viewpager

/**
 * Provides information about a page in the creation workflow, such as the type of information collected
 * by the page as well as some metadata about the page
 */
// TODO: this class should contain info about actions which can be performed on the page
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