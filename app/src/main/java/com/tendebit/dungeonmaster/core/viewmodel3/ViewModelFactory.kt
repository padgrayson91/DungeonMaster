package com.tendebit.dungeonmaster.core.viewmodel3

interface ViewModelFactory<T : ViewModel> {

	fun createNew(): T

}
