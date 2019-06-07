package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.core.viewmodel.ViewModel
import java.lang.IllegalArgumentException

data class PageInsertion(override val range: IntRange, val pages: List<ViewModel>): PageChange {

	init {
		if (range.count() != pages.size) throw IllegalArgumentException("Invalid instruction to insert ${range.count()} pages but only ${pages.count()} were provided")
	}

}
