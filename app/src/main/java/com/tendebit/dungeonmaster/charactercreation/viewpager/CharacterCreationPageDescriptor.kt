package com.tendebit.dungeonmaster.charactercreation.viewpager

/**
 * Provides information about a page in the creation workflow, such as the type of information collected
 * by the page as well as some metadata about the page
 */
class CharacterCreationPageDescriptor(val type: PageType, val actions: List<PageAction>, val indexInGroup: Int = 0) {
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
    }

    override fun hashCode(): Int {
        var result = 12
        result = 31 * result + type.hashCode()
        result = 31 * result + indexInGroup
        return result
    }
}