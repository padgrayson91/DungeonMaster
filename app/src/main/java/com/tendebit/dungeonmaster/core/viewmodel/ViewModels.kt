package com.tendebit.dungeonmaster.core.viewmodel

class ViewModels {
    companion object {
        @JvmStatic
        fun <T> get(tag: String, parent: ViewModelParent, newInstance: T) : T {
            val existingViewModel = parent.getChildViewModel<T>(tag)
            return if (existingViewModel != null) existingViewModel
            else {
                parent.addChildViewModel(tag, newInstance as Any)
                newInstance
            }
        }
    }
}