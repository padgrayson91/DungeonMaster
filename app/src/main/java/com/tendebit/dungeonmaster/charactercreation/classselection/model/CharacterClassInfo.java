package com.tendebit.dungeonmaster.charactercreation.classselection.model;

import com.google.gson.annotations.SerializedName;
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory;
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyGroup;
import com.tendebit.dungeonmaster.core.model.SelectionElement;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class CharacterClassInfo implements SelectionElement {
    public String name;
    public String url;
    @SerializedName("hit_die")
    private int hitDie;
    @SerializedName("proficiency_choices")
    public List<CharacterProficiencyGroup> proficiencyChoices;
    private List<CharacterProficiencyDirectory> proficiencies;

    @Override
    public String toString() {
        return "\nInfo for class " + name + ": \n"
                + " Hit Die: " + hitDie + "\n"
                + " Natural Proficiencies: \n" + proficiencies + "\n"
                + " Proficiency Choices: \n" + proficiencyChoices;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CharacterClassInfo && ((CharacterClassInfo) obj).name.equals(name);
    }

    @NotNull
    @Override
    public String primaryText() {
        return name;
    }

    @NotNull
    @Override
    public String primaryId() {
        return url;
    }
}
