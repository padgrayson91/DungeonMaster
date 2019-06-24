package com.tendebit.dungeonmastercore.platform

import com.tendebit.dungeonmastercore.viewmodel3.ViewModel
import com.tendebit.dungeonmastercore.viewmodel3.ViewModelFactory

/**
 * A [ViewModelManager] is effectively a map from [Long] to [ViewModel];
 * However, when a ViewModel is added to the map, the [ViewModelManager] is responsible for assigning it a key, as opposed to a traditional
 * map where a caller would need to provide the key. This prevents the need for a caller to know anything about the contents of the map aside
 * from the keys which have been provided to it
 */
interface ViewModelManager {

	fun addViewModel(viewModel: ViewModel): Long

	fun removeViewModel(id: Long?)

	fun <T : ViewModel> findViewModel(id: Long?): T?

	fun <T : ViewModel> findOrCreateViewModel(id: Long?, factory: ViewModelFactory<T>): Lookup<T>

	class Lookup<T : ViewModel>(val id: Long, val viewModel: T)

}
