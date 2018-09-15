package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model

import com.google.gson.annotations.SerializedName

/**
 * Top-level list of all available character races as returned by the dnd5e API
 */
class CharacterRaceManifest {

    @SerializedName("results")
    lateinit var characterRaceDirectories: List<CharacterRaceDirectory>
}