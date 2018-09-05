package com.tendebit.dungeonmaster.core.model


interface SelectionState<T : SelectionElement, SelectedType: SelectionElement> {
    val options: List<T>
    var selection: SelectedType?

    fun updateOptions(options: List<T>)
    fun select(option: SelectedType)
}