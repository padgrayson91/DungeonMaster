package com.tendebit.dungeonmaster.charactercreation.viewpager.adapter

import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPageDescriptor


class CharacterCreationPageCollection(val pages: List<CharacterCreationPageDescriptor>) {
    val size = pages.size
    var currentPageIndex = 0

    override fun equals(other: Any?): Boolean {
        return other is CharacterCreationPageCollection && other.pages == pages
    }

    override fun hashCode(): Int {
        return pages.hashCode()
    }

    fun getCurrentPage() : CharacterCreationPageDescriptor {
        return pages[currentPageIndex]
    }
}