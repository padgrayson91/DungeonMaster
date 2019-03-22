package com.tendebit.dungeonmaster.charactercreation2.pager

import com.tendebit.dungeonmaster.core.viewmodel2.Page

interface ViewModelPageCollection : List<Page> {

	val pages: List<Page>

	fun insertPage(viewModel: Page, index: Int)

	fun removePage(index: Int): Page

	fun clear()

}
