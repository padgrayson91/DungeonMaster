package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model

import com.tendebit.dungeonmaster.core.model.SelectableElement

class CharacterRaceDirectory : SelectableElement {
    lateinit var name: String
    lateinit var url: String

    override fun primaryText(): String {
        return name
    }

    override fun primaryId(): String {
        return url
    }
}