package com.tendebit.dungeonmaster.core.platform

interface ViewModelManager {

	fun addViewModel(viewModel: Any): Long

	fun removeViewModel(id: Long?)

	fun <T> findViewModel(id: Long?): T?

}
