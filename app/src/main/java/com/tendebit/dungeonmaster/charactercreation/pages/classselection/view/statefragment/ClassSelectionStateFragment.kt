package com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoService
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.CharacterClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val CLASS_SELECTION_FRAGMENT_TAG = "class_selection_state_fragment"


class ClassSelectionStateFragment : Fragment(), ClassSelectionStateProvider {
    private lateinit var service: CharacterClassInfoService.Impl
    private val stateSubject = BehaviorSubject.create<CharacterClassSelectionState>()
    private var job: Job? = null
    private var stateFragment: CharacterCreationStateFragment? = null
    override val stateChanges = stateSubject as Observable<CharacterClassSelectionState>

    private val classSelectionState = CharacterClassSelectionState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = CharacterClassInfoService.Impl(activity!!)
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            stateFragment?.let {
                stateChanges
                        .filter {it.selection != null}
                        .map { it.selection!! }
                        .subscribe(it.classSelectionObserver)
            }
        }

        retainInstance = true
        if (classSelectionState.options.isEmpty()) {
            loadClassOptions()
        }
    }

    private fun loadClassOptions() {
        job = launch(UI) {
            try {
                val result = async(parent = job) {  service.getCharacterClasses() }.await()
                Log.d("CHARACTER_CREATION", "Got " + result.characterClassDirectories.size + " character classes. The first one is " + result.characterClassDirectories[0].name)
                classSelectionState.updateOptions(result.characterClassDirectories)
                notifyDataChanged()
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error", e)
            }
        }
    }

    override fun onClassSelected(selection: CharacterClassDirectory) {
        if (selection.primaryId() != classSelectionState.selection?.primaryId()) {
            job = launch(UI) {
                try {
                    val result = async(parent = job) {
                        service.getClassInfo(selection)
                    }.await()
                    Log.d("CHARACTER_CREATION", result.toString())
                    classSelectionState.select(result)
                    notifyDataChanged()
                } catch (e: Exception) {
                    Log.e("CHARACTER_CREATION", "Got an error", e)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(classSelectionState)
    }
}