package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.core.viewmodel.SelectableElement

/**
 * Model object corresponding to the basic class info sent by the dnd5e API
 */
class CharacterClassDirectory : SelectableElement {
    @SerializedName("name")
    lateinit var name: String
    @SerializedName("url")
    lateinit var url: String

    override fun toString(): String {
        return "Entry for class $name can be found at $url"
    }

    override fun primaryText(): String {
        return name
    }

    override fun primaryId(): String {
        return url
    }
}