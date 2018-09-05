package com.tendebit.dungeonmaster.charactercreation.raceselection.model

import com.tendebit.dungeonmaster.core.model.SelectionElement

class CharacterRaceDirectory : SelectionElement {
    lateinit var name: String
    lateinit var url: String

    override fun primaryText(): String {
        return name
    }

    override fun primaryId(): String {
        return url
    }
}