package com.tendebit.dungeonmaster.charactercreation.viewpager

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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.ArrayList

// TODO: this class is becoming unwieldy.  Some of the logic for pages can probably be generified to mitigate this
class CharacterCreationState(val db: DnDDatabase, val listState: CharacterListState, val raceState: RaceSelectionState,
                             val classState: ClassSelectionState, val proficiencyState: ProficiencySelectionState,
                             val customInfoState: CustomInfoEntryState) {
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
    var isComplete = false

    private val stateSubject = BehaviorSubject.create<CharacterCreationState>()
    val changes = stateSubject as Observable<CharacterCreationState>

    private val disposables = CompositeDisposable()

    init {
        val networkCallObservables = Arrays.asList(
                classState.networkCallChanges.startWith(0),
                raceState.networkCallChanges.startWith(0)
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
                listState.selectionChanges.subscribe { onSavedCharacterSelected(it) },
                listState.newCharacterCreationStart.subscribe { onNewCharacterCreationStarted() },
                raceState.selection.subscribe { onCharacterRaceSelected(it) },
                classState.selection.subscribe { onCharacterClassSelected(it) },
                proficiencyState.selectionChanges.map { it.first }.subscribe { onProficiencySelectionChanged(it) },
                proficiencyState.completionChanges.subscribe { onProficiencyCompletionChanged(it) },
                customInfoState.changes.subscribe { onCustomDataChanged(it) }

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

    fun saveCharacter(db : DnDDatabase) {
        // TODO: should probably have a separate class to handle this
        job = launch(UI) {
            isLoading = true
            notifyDataChanged()
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
                    db.characterDao().storeCharacter(characterToSave)
                }.await()
                isComplete = true
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

    private fun onNewCharacterCreationStarted() {
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
            proficiencyState.onNewClassSelected(selection)
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

    private fun onProficiencyCompletionChanged(isComplete: Boolean) {
        // If all proficiencies are selected and the next page hasn't been added already
        if (isComplete) {
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
            clearPagesStartingAt(PROFICIENCY_SELECTION_PAGE_INDEX + proficiencyState.proficiencyGroups.size)
        }
        notifyDataChanged()
    }

    private fun onProficiencySelectionChanged(selections: Collection<CharacterProficiencyDirectory>, notify: Boolean = true) {
        selectedProficiencies.clear()
        selectedProficiencies.addAll(selections)
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

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}