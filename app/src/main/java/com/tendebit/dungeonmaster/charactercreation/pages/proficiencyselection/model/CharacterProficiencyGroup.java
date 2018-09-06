package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CharacterProficiencyGroup {
    @SerializedName("choose")
    public int choiceCount;

    @SerializedName("from")
    public List<CharacterProficiencyDirectory> proficiencyOptions;

    @Override
    public String toString() {
        return "Character may choose " + choiceCount + " from:\n"
                + proficiencyOptions.toString();
    }
}
