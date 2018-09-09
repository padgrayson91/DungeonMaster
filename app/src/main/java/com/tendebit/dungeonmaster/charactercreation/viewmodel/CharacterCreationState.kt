package com.tendebit.dungeonmaster.charactercreation.viewmodel

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.ClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.ProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.RaceSelectionState
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import kotlin.collections.ArrayList


class CharacterCreationState {
    private companion object {
        const val CLASS_SELECTION_PAGE_INDEX = 1
        const val PROFICIENCY_SELECTION_PAGE_INDEX = 2
    }

    var currentPage = 0
    var pageCollection = CharacterCreationPageCollection(arrayListOf(CharacterCreationPageDescriptor(
            CharacterCreationPageDescriptor.PageType.RACE_SELECTION, 0)))
    val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    var selectedClass: CharacterClassInfo? = null
    var selectedRace: CharacterRaceDirectory? = null
    var isLoading = false

    private val stateSubject = BehaviorSubject.create<CharacterCreationState>()
    val changes = stateSubject as Observable<CharacterCreationState>

    private val disposables = CompositeDisposable()
    private val classSelectionSubject = BehaviorSubject.create<ClassSelectionState>()
    private val raceSelectionSubject = BehaviorSubject.create<RaceSelectionState>()
    private val proficiencySelectionSubject = BehaviorSubject.create<ProficiencySelectionState>()
    val classSelectionObserver = classSelectionSubject as Observer<ClassSelectionState>
    val raceSelectionObserver = raceSelectionSubject as Observer<RaceSelectionState>
    val proficiencySelectionObserver = proficiencySelectionSubject as Observer<ProficiencySelectionState>

    init {
        val networkCallObservables = Arrays.asList(
                classSelectionSubject.map { it.activeNetworkCalls }.startWith(0),
                raceSelectionSubject.map { it.activeNetworkCalls }.startWith(0)
                // ... etc for other pages ...
        )

        val activeCallCountForChildren: Observable<Int> = Observable.combineLatest(
                networkCallObservables) { counts: Array<out Any> -> counts.map {
            when(it) {
                is Int -> it
                else -> throw IllegalStateException("Call counts should be Ints. Got " + it.javaClass.simpleName + " instead")
            }
        }.sum()
        }

        disposables.addAll(
                activeCallCountForChildren.subscribe { onNetworkCallCountChanged(it) },
                classSelectionSubject.filter { it.selection != null }
                        .map { it.selection!! }.subscribe { onCharacterClassSelected(it) },
                raceSelectionSubject.filter { it.selection != null }
                        .map { it.selection!! }.subscribe { onCharacterRaceSelected(it) },
                proficiencySelectionSubject.subscribe { onProficiencySelectionChanged(it) }

                // ... etc for other pages ...
        )
        notifyDataChanged()
    }

    private fun clearPagesStartingAt(index: Int) {
        if (index >= pageCollection.size) return
        val pagesToKeep = pageCollection.pages.subList(0, index)
        pageCollection = CharacterCreationPageCollection(pagesToKeep)
        if (currentPage >= pageCollection.size) {
            currentPage = pageCollection.size
        }
    }

    private fun addPage(pageDescriptor: CharacterCreationPageDescriptor) {
        val updatedPages = ArrayList(pageCollection.pages)
        updatedPages.add(pageDescriptor)
        pageCollection = CharacterCreationPageCollection(updatedPages)
    }

    fun onPageSelected(selection: Int) {
        currentPage = selection
        notifyDataChanged()
    }

    fun cancelAllSubscriptions() {
        disposables.dispose()
    }

    private fun onCharacterClassSelected(selection: CharacterClassInfo) {
        // Only clear pages if the selection actually changed
        if (selectedClass != selection) {

            selectedClass = selection
            clearPagesStartingAt(PROFICIENCY_SELECTION_PAGE_INDEX)
            notifyDataChanged()
            for (i in 0 until selection.proficiencyChoices.size) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, i))
            }
        }
        currentPage = PROFICIENCY_SELECTION_PAGE_INDEX
        notifyDataChanged()
    }

    private fun onCharacterRaceSelected(selection: CharacterRaceDirectory) {
        if (selectedRace != selection) {
            selectedRace = selection
            if (pageCollection.size - 1 < CLASS_SELECTION_PAGE_INDEX) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, 0))
            }
        }
        currentPage = CLASS_SELECTION_PAGE_INDEX
        notifyDataChanged()
    }

    private fun onProficiencySelectionChanged(state: ProficiencySelectionState) {
        selectedProficiencies.clear()
        selectedProficiencies.addAll(state.selectedProficiencies)
        if (state.areAllProficienciesSelected()) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION, 0)
            )
        } else {
            clearPagesStartingAt(PROFICIENCY_SELECTION_PAGE_INDEX + state.proficiencyGroups.size)
        }
        notifyDataChanged()
    }

    private fun onNetworkCallCountChanged(count: Int) {
        Log.d("CHARACTER_CREATION", "There are now $count async calls awaiting a response")
        isLoading = count > 0
        notifyDataChanged()
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}