package com.tendebit.dungeonmaster.charactercreation.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.CharacterClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.CharacterRaceSelectionState
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationPageDescriptor
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

class CharacterCreationStateFragment : Fragment(), CharacterCreationStateProvider {
    private val classSelectionPage = 1
    private val proficiencySelectionPageStart = 2

    private val stateSubject = BehaviorSubject.create<CharacterCreationState>()
    override val stateChanges = stateSubject as Observable<CharacterCreationState>

    private val creationState = CharacterCreationState()
    private val disposables = CompositeDisposable()
    private val classSelectionSubject = BehaviorSubject.create<CharacterClassSelectionState>()
    private val raceSelectionSubject = BehaviorSubject.create<CharacterRaceSelectionState>()
    val classSelectionObserver = classSelectionSubject as Observer<CharacterClassSelectionState>
    val raceSelectionObserver = raceSelectionSubject as Observer<CharacterRaceSelectionState>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        disposables.addAll(
                stateSubject.map { it.isLoading }.distinctUntilChanged().subscribe { notifyStateChanged() },
                classSelectionSubject.filter { it.selection != null }
                        .map { it.selection!! }.subscribe { onCharacterClassSelected(it) },
                raceSelectionSubject.filter { it.selection != null }
                        .map { it.selection!! }.subscribe { onCharacterRaceSelected(it) },
                classSelectionSubject.map { it.activeNetworkCalls }
                        .subscribe { onNetworkCallCountChanged(it) }
                // ... etc for other pages ...
        )
        creationState.addPage(
                CharacterCreationPageDescriptor(
                        CharacterCreationPageDescriptor.PageType.RACE_SELECTION, 0))
        notifyStateChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }


    override fun onProficiencySelected(selection: CharacterProficiencyDirectory) {
        creationState.selectedProficiencies.add(selection)
    }

    override fun onPageSelected(selection: Int) {
        creationState.currentPage = selection
    }

    private fun onCharacterClassSelected(selection: CharacterClassInfo) {
        // Only clear pages if the selection actually changed
        if (creationState.selectedClass != selection) {

            creationState.selectedClass = selection
            creationState.clearPagesStartingAt(proficiencySelectionPageStart)
            for (i in 0 until selection.proficiencyChoices.size) {
                creationState.addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, i))
            }
        }
        creationState.currentPage = proficiencySelectionPageStart
        notifyStateChanged()
    }

    private fun onCharacterRaceSelected(selection: CharacterRaceDirectory) {
        if (creationState.selectedRace != selection) {
            creationState.selectedRace = selection
            if (creationState.availablePages.size - 1 < classSelectionPage) {
                creationState.addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, 0))
            }
        }
        creationState.currentPage = classSelectionPage
        notifyStateChanged()
    }

    private fun notifyStateChanged() {
        stateSubject.onNext(creationState)
    }

    private fun onNetworkCallCountChanged(count: Int) {
        creationState.isLoading = count > 0
        notifyStateChanged()
    }
}