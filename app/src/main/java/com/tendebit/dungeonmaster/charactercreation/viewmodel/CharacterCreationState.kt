package com.tendebit.dungeonmaster.charactercreation.viewmodel

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.ClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.RaceSelectionState
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*


class CharacterCreationState {
    private companion object {
        const val CLASS_SELECTION_PAGE_INDEX = 1
        const val PROFICIENCY_SELECTION_PAGE_INDEX = 2
    }

    var currentPage = 0
    val availablePages = LinkedList<CharacterCreationPageDescriptor>()
    var selectedClass: CharacterClassInfo? = null
    private var selectedRace: CharacterRaceDirectory? = null
    var isLoading = false

    private val stateSubject = BehaviorSubject.create<CharacterCreationState>()
    val changes = stateSubject as Observable<CharacterCreationState>

    private val disposables = CompositeDisposable()
    private val classSelectionSubject = BehaviorSubject.create<ClassSelectionState>()
    private val raceSelectionSubject = BehaviorSubject.create<RaceSelectionState>()
    val classSelectionObserver = classSelectionSubject as Observer<ClassSelectionState>
    val raceSelectionObserver = raceSelectionSubject as Observer<RaceSelectionState>

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
                        .map { it.selection!! }.subscribe { onCharacterRaceSelected(it) }
                // ... etc for other pages ...
        )
        addPage(
                CharacterCreationPageDescriptor(
                        CharacterCreationPageDescriptor.PageType.RACE_SELECTION, 0))
        notifyDataChanged()
    }

    private fun clearPagesStartingAt(index: Int) {
        if (index >= availablePages.size) return
        availablePages.subList(index, availablePages.size).clear()
        if (currentPage >= availablePages.size) {
            currentPage = availablePages.size
        }
    }

    private fun addPage(pageDescriptor: CharacterCreationPageDescriptor) {
        availablePages.add(pageDescriptor)
    }

    fun onPageSelected(selection: Int) {
        currentPage = selection
    }

    fun cancelAllSubscriptions() {
        disposables.dispose()
    }

    private fun onCharacterClassSelected(selection: CharacterClassInfo) {
        // Only clear pages if the selection actually changed
        if (selectedClass != selection) {

            selectedClass = selection
            clearPagesStartingAt(PROFICIENCY_SELECTION_PAGE_INDEX)
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
            if (availablePages.size - 1 < CLASS_SELECTION_PAGE_INDEX) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, 0))
            }
        }
        currentPage = CLASS_SELECTION_PAGE_INDEX
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