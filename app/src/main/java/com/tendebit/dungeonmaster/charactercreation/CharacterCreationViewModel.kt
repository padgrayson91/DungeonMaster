package com.tendebit.dungeonmaster.charactercreation

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.feature.StoredCharacterSupplier
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
import com.tendebit.dungeonmaster.core.model.AsyncViewModel
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import com.tendebit.dungeonmaster.core.viewmodel.ViewModelParent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Top-level ViewModel for character creation.  Contains references to ViewModels for each individual page
 * of the character creation workflow so that information about the state of those pages can be queried/updated
 * from anywhere
 */
const val TAG = "CHARACTER_CREATION"

class CharacterCreationViewModel(private val characterSupplier: StoredCharacterSupplier, private val pagesViewModel: CharacterCreationPagesViewModel) : AsyncViewModel, ViewModelParent {

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

    private val job: Job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    val selectedProficiencies = TreeSet<CharacterProficiencyDirectory>()
    var selectedClass: CharacterClassInfo? = null
    var selectedRace: CharacterRaceDirectory? = null
    var customInfo = CustomInfo()

    override var activeAsyncCalls = 0

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
    override val asyncCallChanges = PublishSubject.create<Int>()

    init {
        // List of everything that may be waiting on an asynchronous call
        val asyncCallObservables = Arrays.asList(
                classNetworkCalls.startWith(0),
                raceNetworkCalls.startWith(0),
                asyncCallChanges.startWith(0),
                pagesViewModel.asyncCallChanges.startWith(0)
                // ... etc for other pages ...
        )

        // Combine emissions from the above items and get the sum
        val activeCallCountForChildren: Observable<Int> = Observable.combineLatest(
                asyncCallObservables) { counts: Array<out Any> -> counts.map {
            when(it) {
                is Int -> it
                else -> throw IllegalStateException("Call counts should be Ints. Got ${it.javaClass.simpleName} instead")
            }
        }.sum()
        }

        disposables.addAll(
                activeCallCountForChildren.subscribe { onAsyncCallCountChanged(it) },
                pagesViewModel.clearedPages.subscribe { clearChildViewModels(it) }
                )
        notifyDataChanged()
    }

    private fun addCharacterList(viewModel: CharacterListViewModel) {
        disposables.addAll(viewModel.selection.subscribe { onSavedCharacterSelected(it.storedCharacter) },
                viewModel.newCharacterCreationStart.subscribe { onNewCharacterCreationStarted() })
    }

    private fun addClassSelection(viewModel: ClassSelectionViewModel) {
        viewModel.asyncCallChanges.subscribe(classNetworkCalls)
        disposables.add(viewModel.selection.subscribe { onCharacterClassSelected(it) })
    }

    private fun addRaceSelection(viewModel: RaceSelectionViewModel) {
        viewModel.asyncCallChanges.subscribe(raceNetworkCalls)
        disposables.add(viewModel.selection.subscribe { onCharacterRaceSelected(it) })
    }

    private fun addProficiencySelection(viewModel: ProficiencySelectionViewModel) {
        disposables.addAll(
                viewModel.selectionChanges.map { it.first }
                        .subscribe {
                            onProficiencySelectionChanged(it)
                        },
                viewModel.completionChanges.distinctUntilChanged().subscribe { onProficiencyCompletionChanged(it) })
    }

    private fun addCustomInfoEntry(viewModel: CustomInfoEntryViewModel) {
        disposables.add(viewModel.changes.subscribe { onCustomDataChanged(it) })
    }

    override fun <T> getChildViewModel(tag: String) : T? {
        @Suppress("UNCHECKED_CAST")
        return childViewModelMap[tag] as? T
    }

    override fun addChildViewModel(tag: String, child: Any) {
        val previouslyAdded = getChildViewModel<Any>(tag)
        if (previouslyAdded != null) Log.w(TAG, "ViewModel was already added for $tag!")
        childViewModelMap[tag] = child
        when(child) {
            is CharacterListViewModel -> addCharacterList(child)
            is RaceSelectionViewModel -> addRaceSelection(child)
            is ClassSelectionViewModel -> addClassSelection(child)
            is ProficiencySelectionViewModel -> addProficiencySelection(child)
            is CustomInfoEntryViewModel -> addCustomInfoEntry(child)
            else -> throw IllegalArgumentException("${this.javaClass.simpleName} " +
                    "does not expect a child of type ${child.javaClass.simpleName}")
        }
    }

    private fun clearChildViewModels(descriptors: List<CharacterCreationPageDescriptor>) {
        for (descriptor in descriptors) {
            clearChildViewModel(descriptor.viewModelTag)
        }
    }

    override fun clearChildViewModel(tag: String) {
        childViewModelMap.remove(tag)?.let {
            if (it is AttachableViewModel) {
                it.onDetach()
            }
        }
    }

    fun saveCharacter() {
        uiScope.launch {
            onAsyncCallStart()
            try {
                withContext(Dispatchers.Default) {
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
                    characterSupplier.saveCharacter(characterToSave)
                }
                completionSubject.onNext(true)
            } catch (e: Exception) {
                Log.e(TAG, "Got an error while trying to save character", e)
            } finally {
                onAsyncCallFinish()
            }
        }
    }

    fun resetWorkflow() {
        pagesViewModel.resetPages()
    }

    override fun onDetach() {
        disposables.dispose()
        uiScope.launch { job.cancel() }
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

    private fun onAsyncCallCountChanged(count: Int) {
        Log.d(TAG, "There are now $count async calls awaiting a response")
        loadingSubject.onNext(count > 0)
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}