package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyGroup
import com.tendebit.dungeonmaster.core.model.SelectableElement

class CharacterClassInfo : SelectableElement {
    lateinit var name: String
    lateinit var url: String
    @SerializedName("hit_die")
    private val hitDie: Int = 0
    @SerializedName("proficiency_choices")
    lateinit var proficiencyChoices: List<CharacterProficiencyGroup>
    private val proficiencies: List<CharacterProficiencyDirectory>? = null

    override fun toString(): String {
        return ("\nInfo for class " + name + ": \n"
                + " Hit Die: " + hitDie + "\n"
                + " Natural Proficiencies: \n" + proficiencies + "\n"
                + " Proficiency Choices: \n" + proficiencyChoices)
    }

    override fun equals(other: Any?): Boolean {
        return other is CharacterClassInfo && other.name == name
    }

    override fun primaryText(): String {
        return name
    }

    override fun primaryId(): String {
        return url
    }

    override fun hashCode(): Int {
        return primaryId().hashCode()
    }
}