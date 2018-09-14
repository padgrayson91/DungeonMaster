package com.tendebit.dungeonmaster.core.model

import io.reactivex.Flowable
import io.reactivex.Observable


interface SelectionState<T : SelectableElement, SelectedType: SelectableElement> {
    val options: Flowable<List<T>>
    val selection: Observable<SelectedType>

    fun select(option: T)
}