package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.SelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import java.util.UUID

class DndRaceSelectionPageFactory: PageFactory {

	private val ID = UUID.randomUUID().toString()

	private var raceReq: DndRaceRequirement? = null

	private val page = Page(CharacterCreationViewModel2.PageType.RACE_SELECTION, ID) // This factory only produces a single page

	override fun pageFor(requirement: Requirement<*>): Page? {
		if (requirement is DndRaceOptionsRequirement) {
			return page
		}

		if (requirement is DndRaceRequirement) {
			raceReq = requirement
			return page
		}

		return null
	}

	@Suppress("UNCHECKED_CAST")
	override fun applyData(pageid: String, viewModel: Any) {
		if (pageid == ID) {
			(viewModel as? SelectionViewModel<DndRace>)?.selectionRequirement = raceReq
		}
	}

}
