package com.tendebit.dungeonmaster.charactercreation.viewpager.adapter

import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPageDescriptor
import kotlin.math.max
import kotlin.math.min


class CharacterCreationPageCollection(val pages: List<CharacterCreationPageDescriptor>) {
    val size = pages.size

    override fun equals(other: Any?): Boolean {
        return other is CharacterCreationPageCollection && other.pages == pages
    }

    override fun hashCode(): Int {
        return pages.hashCode()
    }

    fun findFirstDifferingIndex(other: CharacterCreationPageCollection) : Int {
        var index = -1
        for (i in 0 until max(size, other.size)) {
            if (i >= min(size, other.size)) {
                index = i
                break
            } else if (pages[i] != other.pages[i]) {
                index = i
                break
            }
        }
        return index
    }
}