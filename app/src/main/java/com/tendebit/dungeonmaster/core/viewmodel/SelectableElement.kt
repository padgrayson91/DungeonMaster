package com.tendebit.dungeonmaster.core.viewmodel

interface SelectableElement : DisplayableElement {
    fun primaryId() : String

    override fun primaryItemActions(): List<ItemAction> {
        return arrayListOf(ItemAction.SELECT)
    }
}