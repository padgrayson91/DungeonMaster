package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import com.google.gson.annotations.SerializedName

class CharacterClassManifest {
    @SerializedName("results")
    lateinit var characterClassDirectories: List<CharacterClassDirectory>
}