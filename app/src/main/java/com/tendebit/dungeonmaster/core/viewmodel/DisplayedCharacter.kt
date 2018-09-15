package com.tendebit.dungeonmaster.core.viewmodel

import com.tendebit.dungeonmaster.core.model.StoredCharacter

class DisplayedCharacter(val storedCharacter: StoredCharacter) : SelectableElement {

    override fun primaryId(): String {
        return storedCharacter.id
    }

    override fun primaryText(): String {
        return storedCharacter.name
    }

    override fun equals(other: Any?): Boolean {
        return other is DisplayedCharacter &&
                other.storedCharacter == storedCharacter
    }

    override fun hashCode(): Int {
        return storedCharacter.hashCode()
    }

    override fun primaryItemActions(): List<ItemAction> {
        return arrayListOf(ItemAction.SELECT)
    }

    override fun secondaryItemActions(): List<ItemAction> {
        return arrayListOf(ItemAction.DELETE)
    }
}