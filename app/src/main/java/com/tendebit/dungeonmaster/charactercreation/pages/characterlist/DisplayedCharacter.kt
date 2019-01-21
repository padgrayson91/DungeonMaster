package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.core.model.StoredCharacter
import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import com.tendebit.dungeonmaster.core.viewmodel.SelectableElement

data class DisplayedCharacter(val storedCharacter: StoredCharacter) : SelectableElement {

    override fun primaryId(): String {
        return storedCharacter.id
    }

    override fun primaryText(): String {
        return storedCharacter.name
    }

    override fun primaryItemActions(): List<ItemAction> {
        return arrayListOf(ItemAction.SELECT)
    }

    override fun secondaryItemActions(): List<ItemAction> {
        return arrayListOf(ItemAction.DELETE)
    }
}