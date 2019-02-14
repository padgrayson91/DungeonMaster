package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionViewModel2
import io.reactivex.disposables.CompositeDisposable
import java.util.LinkedList
import java.util.concurrent.TimeUnit

class CharacterCreationViewModel2(private val blueprint: DndCharacterBlueprint) {

	enum class Pages {
		CLASS_SELECTION,
		RACE_SELECTION,
		PROFICIENCY_SELECTION
	}

	data class PageRange(val start: Int, val end: Int)

	private var mainDisposable = CompositeDisposable()
	private val children = LinkedList<Any>()


	init {
		mainDisposable.add(blueprint.requirements.debounce(50, TimeUnit.MILLISECONDS)
				.subscribe {
					processRequirements(it)
				})
	}

	fun clear() {
		blueprint.clear()
		children.clear()
	}

	fun destroy() {
		children.clear()
		blueprint.destroy()
		mainDisposable.dispose()
	}

	private fun processRequirements(requirements: List<Requirement<*>>) {
		// FIXME: there is almost certainly a pretty kotlin/RxJava way to do this

		var i = 0
		// Iterate over the requirements until we find one for which we have no child ViewModel
		while (i < requirements.size && i < children.size) {
			val requirement = requirements[i]
			val viewmodel = children[i]
			val result = when (requirement) {
				is DndClassRequirement -> processClassRequirement(requirement, viewmodel as? ClassSelectionViewModel2)
				// TODO: others
				else -> throw IllegalArgumentException("Unable to process requirement of type ${requirement.javaClass.simpleName}")
			}
			if (!result) {
				break
			}
			i++
		}

		// Remove any child ViewModels which occur after the one which was missing; in theory we might be able to
		// recycle some, but computing which ones might not be worth it. Assume they are all invalid. New ones will
		// be created by whatever component is consuming this ViewModel
		if (i < children.size) {
			children.subList(i, children.size).clear()
			// TODO: notify UI that a page range has to be removed
		}

		// For the remaining requirements, which do not have corresponding ViewModels, emit a request that such a ViewModel
		// be created. In practice, this will happen indirectly by creating the View, which will be injected with the ViewModel
		while (i < requirements.size) {
			// TODO: map the remaining requirements to a list of Pages items which represent Views/ViewModels which must be created
			i++
		}
	}

	private fun processClassRequirement(requirement: DndClassRequirement, viewModel: ClassSelectionViewModel2?): Boolean {
		if (viewModel == null) {
			return false
		}

		viewModel.requirement = requirement
		return true
	}

}
