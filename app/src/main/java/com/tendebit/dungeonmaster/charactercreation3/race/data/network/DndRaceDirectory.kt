package com.tendebit.dungeonmaster.charactercreation3.race.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

/**
 * Model object corresponding to the basic race info sent by the dnd5e API
 */
internal class DndRaceDirectory {
    @SerializedName("name")
    lateinit var name: String
    @SerializedName("url")
    lateinit var url: String

    override fun toString(): String {
        return "Entry for race $name can be found at $url"
    }

    fun toDndCharacterClass(): DndRace {
        return DndRace(name, url)
    }

}
