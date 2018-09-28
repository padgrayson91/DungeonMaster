package com.tendebit.dungeonmaster.charactercreation.pages.raceselection

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.AttachableViewModel
import com.tendebit.dungeonmaster.charactercreation.TAG
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.AsyncViewModel
import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import com.tendebit.dungeonmaster.core.viewmodel.SelectionViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

/**
 * ViewModel for character race selection
 */
class RaceSelectionViewModel(private val supplier: CharacterRaceInfoSupplier) : SelectionViewModel<CharacterRaceDirectory, CharacterRaceDirectory>, AsyncViewModel, AttachableViewModel {
    private val optionsSubject = BehaviorSubject.create<List<CharacterRaceDirectory>>()
    override val options = optionsSubject.toFlowable(BackpressureStrategy.DROP)!!
    override val selection = BehaviorSubject.create<CharacterRaceDirectory>()
    override var activeAsyncCalls = 0
    override val asyncCallChanges = PublishSubject.create<Int>()
    private var job: Job? = null

    init {
        loadRaceOptions()
    }

    override fun performActions(target: CharacterRaceDirectory, actions: List<ItemAction>) {
        for (action in actions) {
            when(action) {
                ItemAction.HIGHLIGHT -> Log.d(TAG, "Highlighted race")
                ItemAction.SELECT -> select(target)
                else -> throw throw RuntimeException(
                        "${this::class.java.simpleName} unable to perform action ${action.name}")
            }
        }
    }

    override fun onDetach() {
        launch(UI) {
            job?.cancelAndJoin()
        }
    }


    private fun updateOptions(update: List<CharacterRaceDirectory>) {
        this.optionsSubject.onNext(update)
    }

    private fun select(option: CharacterRaceDirectory) {
        this.selection.onNext(option)
    }

    private fun loadRaceOptions() {
        job = launch(UI) {
            try {
                onAsyncCallStart()
                val result = async(parent = job) {  supplier.getCharacterRaces() }.await()
                Log.d(TAG, "Got " + result.characterRaceDirectories.size + " character races. The first one is " + result.characterRaceDirectories[0].name)
                updateOptions(result.characterRaceDirectories)
            } catch (e: Exception) {
                Log.e(TAG, "Got an error", e)
            } finally {
                onAsyncCallFinish()
            }
        }
    }
}