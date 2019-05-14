package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass

/**
 * Top-level list of [DndCharacterClassDirectory] as returned by the dnd5e API
 */
internal class DndCharacterClassManifest {
    @SerializedName("results")
    lateinit var characterClassDirectories: List<DndCharacterClassDirectory>

    fun toClassList(): List<DndCharacterClass> {
        return characterClassDirectories.map { it.toDndCharacterClass() }
    }

}
