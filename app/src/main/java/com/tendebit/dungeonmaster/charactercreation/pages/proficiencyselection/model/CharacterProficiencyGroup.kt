package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model

import com.google.gson.annotations.SerializedName

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
