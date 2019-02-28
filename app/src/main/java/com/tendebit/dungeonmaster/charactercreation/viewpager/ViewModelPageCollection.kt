package com.tendebit.dungeonmaster.charactercreation.viewpager

interface ViewModelPageCollection : List<ViewModel> {

	val pages: List<ViewModel>

	fun insertPage(viewModel: ViewModel, index: Int)

	fun removePages(range: IntRange)

	fun clear()

}
