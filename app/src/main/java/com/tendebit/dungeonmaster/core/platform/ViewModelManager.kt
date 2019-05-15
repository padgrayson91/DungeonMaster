package com.tendebit.dungeonmaster.core.platform

import com.tendebit.dungeonmaster.core.viewmodel3.ViewModel

/**
 * A [ViewModelManager] is effectively a map from [Long] to [Any] (representing ViewModels, which can have any type in this project);
 * However, when a ViewModel is added to the map, the [ViewModelManager] is responsible for assigning it a key, as opposed to a traditional
 * map where a caller would need to provide the key. This prevents the need for a caller to know anything about the contents of the map aside
 * from the keys which have been provided to it
 */
interface ViewModelManager {

	fun addViewModel(viewModel: ViewModel): Long

	fun removeViewModel(id: Long?)

	fun <T> findViewModel(id: Long?): T?

}
