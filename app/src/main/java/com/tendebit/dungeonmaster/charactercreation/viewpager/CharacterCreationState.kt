package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.content.Context
import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListState
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryState
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.model.CustomInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.viewpager.adapter.CharacterCreationPageCollection
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.ArrayList


class CharacterCreationState {
    private companion object {
        const val RACE_SELECTION_PAGE_INDEX = 1
        const val CLASS_SELECTION_PAGE_INDEX = 2
        const val PROFICIENCY_SELECTION_PAGE_INDEX = 3
    }

    var job: Job? = null
    var currentPage = 0
    var pageCollection = CharacterCreationPageCollection(arrayListOf(CharacterCreationPageDescriptor(
            CharacterCreationPageDescriptor.PageType.CHARACTER_LIST, 0)))
    val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    var selectedClass: CharacterClassInfo? = null
    var selectedRace: CharacterRaceDirectory? = null
    var customInfo = CustomInfo()
    var isLoading = false

    private val stateSubject = BehaviorSubject.create<CharacterCreationState>()
    val changes = stateSubject as Observable<CharacterCreationState>

    private val disposables = CompositeDisposable()
    private val savedCharacterSelectionSubject = BehaviorSubject.create<CharacterListState>()
    private val classSelectionSubject = BehaviorSubject.create<ClassSelectionState>()
    private val raceSelectionSubject = BehaviorSubject.create<RaceSelectionState>()
    private val proficiencySelectionSubject = BehaviorSubject.create<ProficiencySelectionState>()
    private val customInfoSubject = BehaviorSubject.create<CustomInfoEntryState>()
    val savedCharacterSelectionObserver = savedCharacterSelectionSubject as Observer<CharacterListState>
    val classSelectionObserver = classSelectionSubject as Observer<ClassSelectionState>
    val raceSelectionObserver = raceSelectionSubject as Observer<RaceSelectionState>
    val proficiencySelectionObserver = proficiencySelectionSubject as Observer<ProficiencySelectionState>
    val customInfoObserver = customInfoSubject as Observer<CustomInfoEntryState>

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

        // TODO: might be better to use combinelatest with all of these to update the overall
        // TODO: page state in a single place
        disposables.addAll(
                activeCallCountForChildren.subscribe { onNetworkCallCountChanged(it) },
                classSelectionSubject.filter { it.selection != null }
                        .map { it.selection!! }.subscribe { onCharacterClassSelected(it) },
                raceSelectionSubject.filter { it.selection != null }
                        .map { it.selection!! }.subscribe { onCharacterRaceSelected(it) },
                proficiencySelectionSubject.subscribe { onProficiencySelectionChanged(it) },
                customInfoSubject.subscribe { onCustomDataChanged(it)},
                savedCharacterSelectionSubject.subscribe {
                    if (it.isNewCharacter) onNewCharacterCreaetionStarted()
                    else {
                        it.selection?.let { onSavedCharacterSelected(it) }
                    }
                }

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

    fun saveCharacter(context: Context) {
        // TODO: should probably have a separate class to handle this
        job = launch(UI) {
            isLoading = true
            try {
                async(parent = job) {
                    val characterToSave = StoredCharacter(
                            name = customInfo.name!!.toString(),
                            heightFeet = customInfo.heightFeet,
                            heightInches = customInfo.heightInches,
                            weight = customInfo.weight!!,
                            proficiencies = ArrayList(selectedProficiencies),
                            race = selectedRace!!,
                            characterClass = selectedClass!!

                    )
                    val db : DnDDatabase = DnDDatabase.getInstance(context.applicationContext)
                    db.characterDao().storeCharacter(characterToSave)
                }.await()
                clearSelf()
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error while trying to save character", e)
            } finally {
                isLoading = false
                notifyDataChanged()
            }
        }
        notifyDataChanged()
    }

    fun cancelAllSubscriptions() {
        disposables.dispose()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun onNewCharacterCreaetionStarted() {
        clearPagesStartingAt(RACE_SELECTION_PAGE_INDEX)
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION, 0))
        currentPage = RACE_SELECTION_PAGE_INDEX
        notifyDataChanged()
    }

    private fun onSavedCharacterSelected(character: StoredCharacter) {
        clearPagesStartingAt(RACE_SELECTION_PAGE_INDEX)
        selectedRace = character.race
        selectedClass = character.characterClass
        selectedProficiencies.clear()
        selectedProficiencies.addAll(character.proficiencies)
        customInfo = CustomInfo()
        customInfo.name = character.name
        customInfo.heightFeet = character.heightFeet
        customInfo.heightInches = character.heightInches
        customInfo.weight = character.weight
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION, 0))
        currentPage = RACE_SELECTION_PAGE_INDEX
        notifyDataChanged()


    }

    private fun onCharacterClassSelected(selection: CharacterClassInfo, notify: Boolean = true) {
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
        if (notify) notifyDataChanged()
    }

    private fun onCharacterRaceSelected(selection: CharacterRaceDirectory, notify: Boolean = true) {
        if (selectedRace != selection) {
            selectedRace = selection
            if (pageCollection.size - 1 < CLASS_SELECTION_PAGE_INDEX) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, 0))
            }
        }
        currentPage = CLASS_SELECTION_PAGE_INDEX
        if (notify) notifyDataChanged()
    }

    private fun onProficiencySelectionChanged(state: ProficiencySelectionState, notify: Boolean = true) {
        selectedProficiencies.clear()
        selectedProficiencies.addAll(state.selectedProficiencies)
        // If all proficiencies are selected and the next page hasn't been added already
        if (state.areAllProficienciesSelected()
                && PROFICIENCY_SELECTION_PAGE_INDEX + state.proficiencyGroups.size == pageCollection.size) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CUSTOM_INFO, 0)
            )
            if (customInfo.isComplete()) {
                // user has info from before that allows them to proceed to confirmation screen
                addPage(
                        CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                                0, true)
                )
            }
        } else {
            clearPagesStartingAt(PROFICIENCY_SELECTION_PAGE_INDEX + state.proficiencyGroups.size)
        }
        if (notify) notifyDataChanged()
    }

    private fun onCustomDataChanged(state: CustomInfoEntryState, notify: Boolean = true) {
        this.customInfo = state.info
        if (state.isEntryComplete() && pageCollection.pages[pageCollection.size - 1].type
                != CharacterCreationPageDescriptor.PageType.CONFIRMATION) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                            0, true)
            )
        } else if(!state.isEntryComplete() && pageCollection.pages[pageCollection.size - 1].type
                == CharacterCreationPageDescriptor.PageType.CONFIRMATION) {
            clearPagesStartingAt(pageCollection.size - 1)
        }
        if (notify) notifyDataChanged()
    }

    private fun onNetworkCallCountChanged(count: Int) {
        Log.d("CHARACTER_CREATION", "There are now $count async calls awaiting a response")
        isLoading = count > 0
        notifyDataChanged()
    }

    private fun clearSelf() {
        currentPage = 0
        pageCollection = CharacterCreationPageCollection(arrayListOf(CharacterCreationPageDescriptor(
                CharacterCreationPageDescriptor.PageType.CHARACTER_LIST, 0)))
        selectedProficiencies.clear()
        selectedClass = null
        selectedRace = null
        customInfo = CustomInfo()
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}