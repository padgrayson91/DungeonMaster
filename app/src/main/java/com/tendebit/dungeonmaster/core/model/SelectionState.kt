package com.tendebit.dungeonmaster.core.model

import io.reactivex.Observable


interface SelectionState<T : SelectableElement, SelectedType: SelectableElement> {
    val options: Observable<List<T>>
    val selection: Observable<SelectedType>

    fun updateOptions(options: List<T>)
    fun select(option: T)
}