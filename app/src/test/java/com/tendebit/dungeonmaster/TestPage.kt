package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModel2
import com.tendebit.dungeonmaster.charactercreation.viewpager.Page
import org.junit.Test

class TestPage {

	@Test
	fun testEqualWhenIdAndTypeMatch() {
		val page1 = Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "an_id")
		val page2 = Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "an_id")
		assert(page1 == page2)
	}

	@Test
	fun testNotEqualWhenNothingMatches() {
		val page1 = Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, "an_id")
		val page2 = Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "another_id")
		assert(page1 != page2)
	}

	@Test
	fun testNotEqualWhenIdsDiffer() {
		val page1 = Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "an_id")
		val page2 = Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "another_id")
		assert(page1 != page2)
	}

	@Test
	fun testNotEqualWhenTypesDiffer() {
		val page1 = Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "an_id")
		val page2 = Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, "an_id")
		assert(page1 != page2)
	}

}
