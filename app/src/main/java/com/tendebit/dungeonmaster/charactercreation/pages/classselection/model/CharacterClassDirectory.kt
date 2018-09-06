package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.core.model.SelectionElement

class CharacterClassDirectory : SelectionElement {
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