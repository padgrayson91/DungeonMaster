package com.tendebit.dungeonmaster.core.viewmodel

interface ViewModelParent {
    fun addChildViewModel(tag: String, child: Any)
    fun <T> getChildViewModel(tag: String) : T?
    fun clearChildViewModel(tag: String)
}