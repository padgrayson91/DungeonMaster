package com.tendebit.dungeonmaster.charactercreation

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
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPagesViewModel
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
class CharacterCreationViewModel(val db: DnDDatabase, val pagesViewModel: CharacterCreationPagesViewModel,
                                 val listViewModel: CharacterListViewModel, val raceViewModel: RaceSelectionViewModel,
                                 val classViewModel: ClassSelectionViewModel, val proficiencyViewModel: ProficiencySelectionViewModel,
                                 val customInfoViewModel: CustomInfoEntryViewModel) {

    var job: Job? = null
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
                proficiencyViewModel.completionChanges.distinctUntilChanged().subscribe { onProficiencyCompletionChanged(it) },
                customInfoViewModel.changes.subscribe { onCustomDataChanged(it) }

                // ... etc for other pages ...
        )
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
        pagesViewModel.startNewCharacterCreation()
        selectedRace = null
        selectedClass = null
        selectedProficiencies.clear()
        customInfo = CustomInfo()
    }

    private fun onSavedCharacterSelected(character: StoredCharacter) {
        selectedRace = character.race
        selectedClass = character.characterClass
        selectedProficiencies.clear()
        selectedProficiencies.addAll(character.proficiencies)
        customInfo = CustomInfo()
        customInfo.name = character.name
        customInfo.heightFeet = character.heightFeet
        customInfo.heightInches = character.heightInches
        customInfo.weight = character.weight
        pagesViewModel.switchToSavedCharacterPage()
        notifyDataChanged()
    }

    private fun onCharacterClassSelected(selection: CharacterClassInfo) {
        // Only clear pages if the selection actually changed
        val isNew = selectedClass != selection
        pagesViewModel.handleCharacterClassSelected(selection, isNew)
        if (isNew) {
            selectedClass = selection
            proficiencyViewModel.onNewClassSelected(selection)
            notifyDataChanged()
        }
    }

    private fun onCharacterRaceSelected(selection: CharacterRaceDirectory) {
        val isNew = selectedRace != selection
        pagesViewModel.handleCharacterRaceSelected(isNew)
        if (isNew) {
            selectedRace = selection
            notifyDataChanged()
        }
    }

    private fun onProficiencyCompletionChanged(isComplete: Boolean) {
        pagesViewModel.handleProficiencyStatusChange(isComplete, customInfo.isComplete())
    }

    private fun onProficiencySelectionChanged(selections: Collection<CharacterProficiencyDirectory>) {
        selectedProficiencies.clear()
        selectedProficiencies.addAll(selections)
        notifyDataChanged()
    }

    private fun onCustomDataChanged(viewModel: CustomInfoEntryViewModel) {
        this.customInfo = viewModel.info
        pagesViewModel.handleCustomDataChanged(viewModel)
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