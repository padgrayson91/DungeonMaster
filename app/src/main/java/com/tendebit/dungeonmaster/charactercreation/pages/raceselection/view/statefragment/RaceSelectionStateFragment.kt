package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.statefragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoService
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.CharacterRaceSelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val RACE_SELECTION_FRAGMENT_TAG = "race_selection_state_fragment"

class RaceSelectionStateFragment : Fragment(){
    private lateinit var service: CharacterRaceInfoService.Impl
    private val stateSubject = BehaviorSubject.create<CharacterRaceSelectionState>()
    private var job: Job? = null
    private var stateFragment: CharacterCreationStateFragment? = null
    val stateChanges = stateSubject as Observable<CharacterRaceSelectionState>

    private val raceSelectionState = CharacterRaceSelectionState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = CharacterRaceInfoService.Impl(activity!!)
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            stateFragment?.let {
                stateChanges
                        .subscribe(it.raceSelectionObserver)
            }
        }

        retainInstance = true
        if (raceSelectionState.options.isEmpty()) {
            loadClassOptions()
        }
    }

    private fun loadClassOptions() {
        job = launch(UI) {
            raceSelectionState.onNetworkCallStart()
            notifyDataChanged()
            try {
                val result = async(parent = job) {  service.getCharacterRaces() }.await()
                Log.d("CHARACTER_CREATION", "Got " + result.characterRaceDirectories.size + " character races. The first one is " + result.characterRaceDirectories[0].name)
                raceSelectionState.updateOptions(result.characterRaceDirectories)
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error", e)
            } finally {
                raceSelectionState.onNetworkCallFinish()
                notifyDataChanged()
            }
        }
    }

    fun onRaceSelected(selection: CharacterRaceDirectory) {
        if (selection.primaryId() != raceSelectionState.selection?.primaryId()) {
            raceSelectionState.select(selection)
            notifyDataChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(raceSelectionState)
    }
}