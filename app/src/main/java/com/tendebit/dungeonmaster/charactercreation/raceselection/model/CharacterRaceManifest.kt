package com.tendebit.dungeonmaster.charactercreation.raceselection.model

import com.google.gson.annotations.SerializedName

class CharacterRaceManifest {

    @SerializedName("results")
    lateinit var characterRaceDirectories: List<CharacterRaceDirectory>
}