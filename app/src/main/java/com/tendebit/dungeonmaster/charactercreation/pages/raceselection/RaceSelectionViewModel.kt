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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for character race selection
 */
class RaceSelectionViewModel(private val supplier: CharacterRaceInfoSupplier) : SelectionViewModel<CharacterRaceDirectory, CharacterRaceDirectory>, AsyncViewModel, AttachableViewModel {
    private val optionsSubject = BehaviorSubject.create<List<CharacterRaceDirectory>>()
    override val options = optionsSubject.toFlowable(BackpressureStrategy.DROP)!!
    override val selection = BehaviorSubject.create<CharacterRaceDirectory>()
    override var activeAsyncCalls = 0
    override val asyncCallChanges = PublishSubject.create<Int>()
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

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
        uiScope.launch {
            job.cancel()
        }
    }


    private fun updateOptions(update: List<CharacterRaceDirectory>) {
        this.optionsSubject.onNext(update)
    }

    private fun select(option: CharacterRaceDirectory) {
        this.selection.onNext(option)
    }

    private fun loadRaceOptions() {
        uiScope.launch {
            try {
                onAsyncCallStart()
                val result = withContext(Dispatchers.Default) {  supplier.getCharacterRaces() }
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