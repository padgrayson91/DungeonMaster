package com.tendebit.dungeonmaster.charactercreation.classselection.model;

import com.google.gson.annotations.SerializedName;
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory;

import java.util.List;

public class CharacterClassManifest {

    @SerializedName("results")
    public List<CharacterClassDirectory> characterClassDirectories;
}
