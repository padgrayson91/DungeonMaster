package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.Disabled
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ListItemState
import com.tendebit.dungeonmaster.charactercreation3.Locked
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Represents as group of [DndProficiency] from which the user may make selections
 * @param initialOptions the initial [ItemState] for each available [DndProficiency]. For creating a new character, these
 * should all be [Normal]
 * @param choiceCount the total number of selections that a user is allowed to make from this group
 */
class DndProficiencyGroup(initialOptions: List<ItemState<DndProficiency>>, private val choiceCount: Int) {

	/**
	 * The current [ItemState] for all the [DndProficiency] items in this group
	 */
	val options = ArrayList(initialOptions)

	/**
	 * A list of all [Selected] items from this group
	 */
	val selections : List<ItemState<DndProficiency>>
		get() { return options.filter { it is Selected }}

	private val externalSelectionChanges = PublishSubject.create<ListItemState<DndProficiency>>()
	private val internalSelectionChanges = PublishSubject.create<ListItemState<DndProficiency>>()

	/**
	 * An [Observable] which will emit whenever the [ItemState] for any item in this group changes
	 */
	val selectionChanges : Observable<ListItemState<DndProficiency>> = internalSelectionChanges.mergeWith(externalSelectionChanges)

	/**
	 * An [Observable] which will emit [ItemState] changes driven
	 */
	internal val outboundSelectionChanges = internalSelectionChanges as Observable<ListItemState<DndProficiency>>

	val remainingChoices: Int
		get() { return choiceCount - selections.size }

	/**
	 * Invoke this method when a user has selected the proficiency item at the specified index
	 * @param index the index to select
	 * @throws IndexOutOfBoundsException if the index is outside the list
	 * @throws IllegalArgumentException if the [ItemState] at the provided index is not [Normal]
	 */
	fun select(index: Int) {
		if (index < 0 || index >= options.size) { throw IndexOutOfBoundsException("Attempting to select a proficiency outside the list") }
		val itemState = options[index] as? Normal<DndProficiency> ?: throw IllegalArgumentException("Proficiency at index $index cannot be selected")
		val selectedState = Selected(itemState.item)
		options[index] = selectedState
		internalSelectionChanges.onNext(ListItemState(index, selectedState))
		if (remainingChoices == 0) {
			onSelectionComplete()
		}
	}

	/**
	 * Invoke this method when a user has deselected a previously selected proficiency item at the specified index
	 * @param index the index to deselect
	 * @throws IndexOutOfBoundsException if the index is outside the list
	 * @throws IllegalArgumentException if the [ItemState] at the provided index is not [Selected]
	 */
	fun deselect(index: Int) {
		if (index < 0 || index >= options.size) { throw IndexOutOfBoundsException("Attempting to deselect a proficiency outside the list") }
		val itemState = options[index] as? Selected<DndProficiency> ?: throw IllegalArgumentException("Proficiency at index $index cannot be deselected")
		val normalState = Normal(itemState.item)
		options[index] = normalState
		internalSelectionChanges.onNext(ListItemState(index, normalState))
		if (remainingChoices == 1) {
			onSelectionIncomplete()
		}
	}

	/**
	 * Invoke this method when a proficiency from a different [DndProficiencyGroup] has been selected, which will cause a matching proficiency
	 * in this group (if any is present) to take on the [Locked] state
	 */
	fun onExternalSelection(proficiency: DndProficiency) {
		val localIndex = options.indexOfFirst { it.item != null && it.item == proficiency }
		if (localIndex < 0) return
		val lockedState = Locked(proficiency)
		options[localIndex] = lockedState
		externalSelectionChanges.onNext(ListItemState(localIndex, lockedState))
	}

	/**
	 * Invoke this method when a proficiency from a different [DndProficiencyGroup] has been deselected, which will cause a matching proficiency
	 * in this group (if any is present) to take on either the [Disabled] state if [remainingChoices] == 0 or the [Normal] state
	 */
	fun onExternalDeselection(proficiency: DndProficiency) {
		val localIndex = options.indexOfFirst { it.item != null && it.item == proficiency && it is Locked }
		if (localIndex < 0) return
		val deselectedState = if (remainingChoices == 0) Disabled(proficiency) else Normal(proficiency)
		options[localIndex] = deselectedState
		externalSelectionChanges.onNext(ListItemState(localIndex, deselectedState))
	}

	private fun onSelectionComplete() {
		options.asSequence()
				.forEachIndexed { index, itemState ->
					if (itemState is Normal) {
						val disabledState = Disabled(itemState.item)
						options[index] = disabledState
						internalSelectionChanges.onNext(ListItemState(index, disabledState))
					}
				}
	}

	private fun onSelectionIncomplete() {
		options.asSequence()
				.forEachIndexed { index, itemState ->
					if (itemState is Disabled) {
						val normalState = Normal(itemState.item)
						options[index] = normalState
						internalSelectionChanges.onNext(ListItemState(index, normalState))
					}
				}
	}

}