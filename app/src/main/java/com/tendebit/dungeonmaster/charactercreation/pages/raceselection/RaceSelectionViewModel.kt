package com.tendebit.dungeonmaster.charactercreation.pages.raceselection

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.NetworkUIState
import com.tendebit.dungeonmaster.core.model.SelectionViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class RaceSelectionViewModel(val supplier: CharacterRaceInfoSupplier) : SelectionViewModel<CharacterRaceDirectory, CharacterRaceDirectory>, NetworkUIState {
    val optionsSubject = BehaviorSubject.create<List<CharacterRaceDirectory>>()
    override val options = optionsSubject.toFlowable(BackpressureStrategy.DROP)
    override val selection = BehaviorSubject.create<CharacterRaceDirectory>()
    override var activeNetworkCalls = 0
    override val networkCallChanges = PublishSubject.create<Int>()
    private var job: Job? = null

    init {
        loadRaceOptions()
    }

    private fun updateOptions(update: List<CharacterRaceDirectory>) {
        this.optionsSubject.onNext(update)
    }

    override fun select(option: CharacterRaceDirectory) {
        this.selection.onNext(option)
    }

    override fun cancelAllCalls() {
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun loadRaceOptions() {
        job = launch(UI) {
            try {
                onNetworkCallStart()
                val result = async(parent = job) {  supplier.getCharacterRaces() }.await()
                Log.d("CHARACTER_CREATION", "Got " + result.characterRaceDirectories.size + " character races. The first one is " + result.characterRaceDirectories[0].name)
                updateOptions(result.characterRaceDirectories)
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error", e)
            } finally {
                onNetworkCallFinish()
            }
        }
    }
}