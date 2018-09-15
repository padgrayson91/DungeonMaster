package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model

import com.google.gson.annotations.SerializedName

/**
 * Model for a collection of proficiencies which may be chosen by a user in conjunction with some
 * other character feature such as class.  This represents the data as returned by the dnd5e API
 */
class CharacterProficiencyGroup {
    @SerializedName("choose")
    var choiceCount: Int = 0

    @SerializedName("from")
    lateinit var proficiencyOptions: List<CharacterProficiencyDirectory>

    override fun toString(): String {
        return ("Character may choose " + choiceCount + " from:\n"
                + proficiencyOptions.toString())
    }
}
