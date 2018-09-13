package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.model.CustomInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionViewModel
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

// TODO: Class may be best split so that page logic is handled by a separate ViewModel, while this class
// TODO: should just coordinate passing data from one ViewModel to another when needed
class CharacterCreationViewModel(val db: DnDDatabase, val listViewModel: CharacterListViewModel, val raceViewModel: RaceSelectionViewModel,
                                 val classViewModel: ClassSelectionViewModel, val proficiencyViewModel: ProficiencySelectionViewModel,
                                 val customInfoViewModel: CustomInfoEntryViewModel) {

    var job: Job? = null
    var currentPage = 0
    var pageCollection = CharacterCreationPageCollection(arrayListOf(CharacterCreationPageDescriptor(
            CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)))
    val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    var selectedClass: CharacterClassInfo? = null
    var selectedRace: CharacterRaceDirectory? = null
    var customInfo = CustomInfo()
    var isLoading = false
    var isComplete = false

    private val stateSubject = BehaviorSubject.create<CharacterCreationViewModel>()
    val changes = stateSubject as Observable<CharacterCreationViewModel>

    private val disposables = CompositeDisposable()

    init {
        val networkCallObservables = Arrays.asList(
                classViewModel.networkCallChanges.startWith(0),
                raceViewModel.networkCallChanges.startWith(0)
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
                listViewModel.selection.subscribe { onSavedCharacterSelected(it) },
                listViewModel.newCharacterCreationStart.subscribe { onNewCharacterCreationStarted() },
                raceViewModel.selection.subscribe { onCharacterRaceSelected(it) },
                classViewModel.selection.subscribe { onCharacterClassSelected(it) },
                proficiencyViewModel.selectionChanges.map { it.first }.subscribe { onProficiencySelectionChanged(it) },
                proficiencyViewModel.completionChanges.subscribe { onProficiencyCompletionChanged(it) },
                customInfoViewModel.changes.subscribe { onCustomDataChanged(it) }

                // ... etc for other pages ...
        )
        notifyDataChanged()
    }

    private fun clearPagesStartingAt(index: Int) {
        if (index >= pageCollection.size || index < 0) return
        val pagesToKeep = pageCollection.pages.subList(0, index)
        pageCollection = CharacterCreationPageCollection(pagesToKeep)
        if (currentPage >= pageCollection.size) {
            currentPage = pageCollection.size
        }
    }

    private fun clearPagesAfter(pageType: CharacterCreationPageDescriptor.PageType) {
        val startIndex = findStartOfGroup(pageType)
        var index = -1
        if (startIndex >= 0) {
            for (i in startIndex until pageCollection.size) {
                if (pageCollection.pages[i].type != pageType) {
                    index = i
                    break
                }
            }
        }
        clearPagesStartingAt(index)
    }

    private fun findStartOfGroup(pageType: CharacterCreationPageDescriptor.PageType) : Int {
        var index = -1
        for (i in 0 until pageCollection.size) {
            if (pageCollection.pages[i].type == pageType) {
                index = i
                break
            }
        }
        return index
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

    // TODO: functionality to clear state (will require substates to expose similar functionality)

    private fun onNewCharacterCreationStarted() {
        clearPagesAfter(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION))
        currentPage = findStartOfGroup(CharacterCreationPageDescriptor.PageType.RACE_SELECTION)
        notifyDataChanged()
    }

    private fun onSavedCharacterSelected(character: StoredCharacter) {
        clearPagesAfter(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)
        selectedRace = character.race
        selectedClass = character.characterClass
        selectedProficiencies.clear()
        selectedProficiencies.addAll(character.proficiencies)
        customInfo = CustomInfo()
        customInfo.name = character.name
        customInfo.heightFeet = character.heightFeet
        customInfo.heightInches = character.heightInches
        customInfo.weight = character.weight
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION))
        currentPage = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CONFIRMATION)
        notifyDataChanged()


    }

    private fun onCharacterClassSelected(selection: CharacterClassInfo, notify: Boolean = true) {
        // Only clear pages if the selection actually changed
        if (selectedClass != selection) {

            selectedClass = selection
            clearPagesAfter(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION)
            proficiencyViewModel.onNewClassSelected(selection)
            notifyDataChanged()
            for (i in 0 until selection.proficiencyChoices.size) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, i))
            }
        }
        currentPage = findStartOfGroup(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION)
        if (notify) notifyDataChanged()
    }

    private fun onCharacterRaceSelected(selection: CharacterRaceDirectory, notify: Boolean = true) {
        if (selectedRace != selection) {
            selectedRace = selection
            if (findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION) == -1) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION))
            }
        }
        currentPage = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION)
        if (notify) notifyDataChanged()
    }

    private fun onProficiencyCompletionChanged(isComplete: Boolean) {
        // If all proficiencies are selected and the next page hasn't been added already
        if (isComplete) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CUSTOM_INFO)
            )
            if (customInfo.isComplete()) {
                // user has info from before that allows them to proceed to confirmation screen
                addPage(
                        CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                                0, true)
                )
            }
        } else {
            clearPagesAfter(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION)
        }
        notifyDataChanged()
    }

    private fun onProficiencySelectionChanged(selections: Collection<CharacterProficiencyDirectory>, notify: Boolean = true) {
        selectedProficiencies.clear()
        selectedProficiencies.addAll(selections)
        if (notify) notifyDataChanged()
    }

    private fun onCustomDataChanged(viewModel: CustomInfoEntryViewModel, notify: Boolean = true) {
        this.customInfo = viewModel.info
        if (viewModel.isEntryComplete() && pageCollection.pages[pageCollection.size - 1].type
                != CharacterCreationPageDescriptor.PageType.CONFIRMATION) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                            0, true)
            )
        } else if(!viewModel.isEntryComplete() && pageCollection.pages[pageCollection.size - 1].type
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