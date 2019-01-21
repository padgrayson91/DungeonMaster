package com.tendebit.dungeonmaster.charactercreation.viewpager

/**
 * Provides information about a page in the creation workflow, such as the type of information collected
 * by the page as well as some metadata about the page
 */
data class CharacterCreationPageDescriptor(val type: PageType, val actions: List<PageAction>, val indexInGroup: Int = 0,
                                      val viewModelTag: String) {
    enum class PageType {
        CHARACTER_LIST,
        RACE_SELECTION,
        CLASS_SELECTION,
        PROFICIENCY_SELECTION,
        CUSTOM_INFO,
        CONFIRMATION
    }
}