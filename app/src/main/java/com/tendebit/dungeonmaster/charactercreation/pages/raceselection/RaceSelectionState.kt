package com.tendebit.dungeonmaster.charactercreation.pages.raceselection

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.NetworkUIState
import com.tendebit.dungeonmaster.core.model.SelectionState
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class RaceSelectionState(val supplier: CharacterRaceInfoSupplier) : SelectionState<CharacterRaceDirectory, CharacterRaceDirectory>, NetworkUIState {
    override val options = ArrayList<CharacterRaceDirectory>()
    override var selection: CharacterRaceDirectory? = null
    override var activeNetworkCalls = 0
    private var job: Job? = null
    private val stateSubject = BehaviorSubject.create<RaceSelectionState>()
    val stateChanges = stateSubject as Observable<RaceSelectionState>

    init {
        loadRaceOptions()
    }

    override fun updateOptions(options: List<CharacterRaceDirectory>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun select(option: CharacterRaceDirectory) {
        this.selection = option
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
                notifyDataChanged()
                val result = async(parent = job) {  supplier.getCharacterRaces() }.await()
                Log.d("CHARACTER_CREATION", "Got " + result.characterRaceDirectories.size + " character races. The first one is " + result.characterRaceDirectories[0].name)
                updateOptions(result.characterRaceDirectories)
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error", e)
            } finally {
                onNetworkCallFinish()
                notifyDataChanged()
            }
        }
    }

    fun onRaceSelected(selectedRace: CharacterRaceDirectory) {
        this.selection = selectedRace
        notifyDataChanged()
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }
}