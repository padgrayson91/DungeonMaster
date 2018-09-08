package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model

class CharacterProficiencyDirectory {
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
}
