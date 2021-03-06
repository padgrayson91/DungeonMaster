package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model

import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import com.tendebit.dungeonmaster.core.viewmodel.SelectableElement

/**
 * Model representing basic proficiency info as provided by the dnd5e API
 */
class CharacterProficiencyDirectory : SelectableElement, Comparable<CharacterProficiencyDirectory> {
    lateinit var name: String
    lateinit var url: String

    override fun toString(): String {
        return "Entry for proficiency $name can be found at $url\n"
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is CharacterProficiencyDirectory && other.url == url
    }

    override fun primaryText(): String {
        return name
    }

    override fun primaryId(): String {
        return url
    }

    override fun compareTo(other: CharacterProficiencyDirectory): Int {
        return other.url.compareTo(url)
    }

    override fun primaryItemActions(): List<ItemAction> {
        return arrayListOf(
                ItemAction.SELECT,
                ItemAction.HIGHLIGHT)
    }

}
