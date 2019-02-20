package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import java.util.LinkedList

class CompositePageFactory: PageFactory {

	private val children = LinkedList<PageFactory>()
	private val pageIdToFactory = HashMap<String, PageFactory>()

	override fun pageFor(requirement: Requirement<*>): Page? {
		for (factory in children) {
			val page = factory.pageFor(requirement)
			if (page != null) {
				pageIdToFactory[page.id] = factory
				return page
			}
		}

		return null
	}

	override fun applyData(pageid: String, viewModel: Any) {
		pageIdToFactory[pageid]?.applyData(pageid, viewModel)
	}

	fun addAll(vararg factories: PageFactory) {
		children.addAll(factories)
	}

}
