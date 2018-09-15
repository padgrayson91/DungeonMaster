package com.tendebit.dungeonmaster.core.viewmodel

import io.reactivex.Flowable
import io.reactivex.Observable


interface SelectionViewModel<T : SelectableElement, SelectedType: SelectableElement> {
    val options: Flowable<List<T>>
    val selection: Observable<SelectedType>

    fun select(option: T)
}