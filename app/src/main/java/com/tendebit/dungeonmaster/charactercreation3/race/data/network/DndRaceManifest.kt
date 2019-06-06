package com.tendebit.dungeonmaster.charactercreation3.race.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

/**
 * Top-level list of [DndRaceDirectory] as returned by the dnd5e API
 */
internal class DndRaceManifest {
    @SerializedName("results")
    lateinit var characterRaceDirectories: List<DndRaceDirectory>

    fun toClassList(): List<DndRace> {
        return characterRaceDirectories.map { it.toDndCharacterClass() }
    }

}
