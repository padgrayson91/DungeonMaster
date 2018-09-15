package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import com.google.gson.annotations.SerializedName

/**
 * Top-level list of [CharacterClassDirectory] as returned by the dnd5e API
 */
class CharacterClassManifest {
    @SerializedName("results")
    lateinit var characterClassDirectories: List<CharacterClassDirectory>
}