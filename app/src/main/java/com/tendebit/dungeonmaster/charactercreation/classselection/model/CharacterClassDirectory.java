package com.tendebit.dungeonmaster.charactercreation.classselection.model;

import com.google.gson.annotations.SerializedName;
import com.tendebit.dungeonmaster.core.SelectionElement;

import org.jetbrains.annotations.NotNull;

public class CharacterClassDirectory implements SelectionElement {
    @SerializedName("name")
    public String name;
    @SerializedName("url")
    public String url;

    @Override
    public String toString() {
        return "Entry for class " + name + " can be found at " + url;
    }

    @NotNull
    @Override
    public String primaryText() {
        return name;
    }
}
