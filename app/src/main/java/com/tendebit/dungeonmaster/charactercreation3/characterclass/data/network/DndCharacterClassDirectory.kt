package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass

/**
 * Model object corresponding to the basic class info sent by the dnd5e API
 */
internal class DndCharacterClassDirectory {
    @SerializedName("name")
    lateinit var name: String
    @SerializedName("url")
    lateinit var url: String

    override fun toString(): String {
        return "Entry for class $name can be found at $url"
    }

    fun toDndCharacterClass(): DndCharacterClass {
        return DndCharacterClass(name, url)
    }

}
