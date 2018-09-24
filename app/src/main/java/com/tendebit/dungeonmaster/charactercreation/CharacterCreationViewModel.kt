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
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPageDescriptor
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPagesViewModel
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Top-level ViewModel for character creation.  Contains references to ViewModels for each individual page
 * of the character creation workflow so that information about the state of those pages can be queried/updated
 * from anywhere
 */
class CharacterCreationViewModel {

    companion object {
        const val ARG_VIEW_MODEL_TAG = "com.tendebit.dungeonmaster.VIEW_MODEL_TAG"

        const val TAG_CHARACTER_LIST = "saved_character_list"
        const val TAG_RACE_LIST = "race_selection"
        const val TAG_CLASS_LIST = "class_selection"
        const val TAG_PROFICIENCY_SELECTION = "proficiency_selection"
        const val TAG_CUSTOM_ENTRY = "custom_info_entry"
        const val TAG_CONFIRMATION = "confirmation"
        const val TAG_REVIEW = "review_details"
    }

    private var job: Job? = null
    val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    var pagesViewModel = CharacterCreationPagesViewModel()
    var selectedClass: CharacterClassInfo? = null
    var selectedRace: CharacterRaceDirectory? = null
    var customInfo = CustomInfo()

    private val stateSubject = BehaviorSubject.create<CharacterCreationViewModel>()
    private val loadingSubject = BehaviorSubject.create<Boolean>()
    private val completionSubject = PublishSubject.create<Boolean>()
    val changes = stateSubject as Observable<CharacterCreationViewModel>
    val loadingChanges = loadingSubject as Observable<Boolean>
    val completionChanges = completionSubject as Observable<Boolean>

    private val disposables = CompositeDisposable()
    private val childViewModelMap = HashMap<String, Any>()

    private val classNetworkCalls = PublishSubject.create<Int>()
    private val raceNetworkCalls = PublishSubject.create<Int>()

    init {
        val networkCallObservables = Arrays.asList(
                classNetworkCalls.startWith(0),
                raceNetworkCalls.startWith(0)
                // ... etc for other pages ...
        )

        val activeCallCountForChildren: Observable<Int> = Observable.combineLatest(
                networkCallObservables) { counts: Array<out Any> -> counts.map {
            when(it) {
                is Int -> it
                else -> throw IllegalStateException("Call counts should be Ints. Got ${it.javaClass.simpleName} instead")
            }
        }.sum()
        }

        disposables.addAll(
                activeCallCountForChildren.subscribe { onNetworkCallCountChanged(it) },
                pagesViewModel.clearedPages.subscribe { clearChildViewModels(it) }
                )
        notifyDataChanged()
    }

    fun addCharacterList(tag: String, viewModel: CharacterListViewModel) {
        // TODO: should warn if an existing viewmodel is being overwritten, and also clear it
        childViewModelMap[tag] = viewModel
        disposables.addAll(viewModel.selection.subscribe { onSavedCharacterSelected(it.storedCharacter) },
                viewModel.newCharacterCreationStart.subscribe { onNewCharacterCreationStarted() })
    }

    fun addClassSelection(tag: String, viewModel: ClassSelectionViewModel) {
        // TODO: should warn if an existing viewmodel is being overwritten, and also clear it
        childViewModelMap[tag] = viewModel
        viewModel.networkCallChanges.subscribe(classNetworkCalls)
        disposables.add(viewModel.selection.subscribe { onCharacterClassSelected(it) })
    }

    fun addRaceSelection(tag: String, viewModel: RaceSelectionViewModel) {
        // TODO: should warn if an existing viewmodel is being overwritten, and also clear it
        childViewModelMap[tag] = viewModel
        viewModel.networkCallChanges.subscribe(raceNetworkCalls)
        disposables.add(viewModel.selection.subscribe { onCharacterRaceSelected(it) })

    }

    fun addProficiencySelection(tag: String, viewModel: ProficiencySelectionViewModel) {
        childViewModelMap[tag] = viewModel
        disposables.addAll(
                viewModel.selectionChanges.map { it.first }
                        .subscribe {
                            onProficiencySelectionChanged(it)
                        },
                viewModel.completionChanges.distinctUntilChanged().subscribe { onProficiencyCompletionChanged(it) })
    }

    fun addCustomInfoEntry(tag: String, viewModel: CustomInfoEntryViewModel) {
        childViewModelMap[tag] = viewModel
        disposables.add(viewModel.changes.subscribe { onCustomDataChanged(it) })
    }

    fun <T> getChildViewModel(tag: String) : T? {
        @Suppress("UNCHECKED_CAST")
        return childViewModelMap[tag] as? T
    }

    private fun clearChildViewModels(descriptors: List<CharacterCreationPageDescriptor>) {
        for (descriptor in descriptors) {
            clearChildViewModel(descriptor.viewModelTag)
        }
    }

    private fun clearChildViewModel(tag: String) {
        childViewModelMap.remove(tag)?.let {
            if (it is AttachableViewModel) {
                it.onDetach()
            }
        }
    }

    fun saveCharacter(db : DnDDatabase) {
        // TODO: should probably have a separate class to handle this
        job = launch(UI) {
            loadingSubject.onNext(true)
            try {
                async(parent = job) {
                    val characterToSave = StoredCharacter(
                            id = UUID.randomUUID().toString(),
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
                completionSubject.onNext(true)
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error while trying to save character", e)
            } finally {
                loadingSubject.onNext(false)
            }
        }
    }

    fun resetWorkflow() {
        pagesViewModel.resetPages()
    }

    fun cancelAllSubscriptions() {
        disposables.dispose()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

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
        selectedClass = selection
        if (isNew) {
            clearChildViewModel(TAG_PROFICIENCY_SELECTION)
            notifyDataChanged()
        }
        pagesViewModel.handleCharacterClassSelected(selection, isNew)
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
        loadingSubject.onNext(count > 0)
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}