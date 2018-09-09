package com.tendebit.dungeonmaster.core.model


interface SelectionState<T : SelectableElement, SelectedType: SelectableElement> {
    val options: List<T>
    var selection: SelectedType?

    fun updateOptions(options: List<T>)
    fun select(option: T)
}