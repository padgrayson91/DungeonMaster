package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model;

@SuppressWarnings("unused")
public class CharacterProficiencyDirectory {
    public String name;
    private String url;

    @Override
    public String toString() {
        return "Entry for proficiency " + name + " can be found at " + url + "\n";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CharacterProficiencyDirectory && ((CharacterProficiencyDirectory) obj).name.equals(name);
    }
}
