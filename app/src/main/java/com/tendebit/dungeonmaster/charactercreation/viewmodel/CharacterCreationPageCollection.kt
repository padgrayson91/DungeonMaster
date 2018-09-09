package com.tendebit.dungeonmaster.charactercreation.viewmodel


class CharacterCreationPageCollection(val pages: List<CharacterCreationPageDescriptor>) {
    val size = pages.size

    override fun equals(other: Any?): Boolean {
        return other is CharacterCreationPageCollection && other.pages == pages
    }

    override fun hashCode(): Int {
        return pages.hashCode()
    }
}