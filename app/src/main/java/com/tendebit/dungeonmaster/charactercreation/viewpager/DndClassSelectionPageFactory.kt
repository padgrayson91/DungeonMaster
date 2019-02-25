package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.SelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import java.util.UUID

class DndClassSelectionPageFactory: PageFactory<SelectionViewModel<DndClass>> {

	private val ID = UUID.randomUUID().toString()

	private var classReq: DndClassRequirement? = null

	private val page = Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, ID) // This factory only produces a single page

	override fun pageFor(requirement: Requirement<*>): Page? {
		if (requirement is DndClassOptionsRequirement) {
			return page
		}

		if (requirement is DndClassRequirement) {
			classReq = requirement
			return page
		}

		return null
	}

	override fun applyData(viewModel: SelectionViewModel<DndClass>) {
		if (viewModel.id == ID) {
			viewModel.selectionRequirement = classReq
		}
	}

}
