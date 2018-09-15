package com.tendebit.dungeonmaster.core.viewmodel

interface SelectableElement : DisplayableElement {
    fun primaryId() : String

    override fun primaryItemActions(): List<ItemAction> {
        // TODO: highlighting should not be part of the default behavior
        return arrayListOf(
                ItemAction.SELECT,
                ItemAction.HIGHLIGHT)
    }
}