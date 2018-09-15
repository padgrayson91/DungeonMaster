package com.tendebit.dungeonmaster.core.viewmodel

import java.util.*

interface DisplayableElement {
    fun primaryText() : String

    fun primaryItemActions() : List<ItemAction> {
        return Collections.emptyList()
    }

    fun secondaryItemActions() : List<ItemAction> {
        return Collections.emptyList()
    }
}