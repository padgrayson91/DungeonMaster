package com.tendebit.dungeonmastercore.viewmodel3

interface ViewModelFactory<T : ViewModel> {

	fun createNew(): T

}
