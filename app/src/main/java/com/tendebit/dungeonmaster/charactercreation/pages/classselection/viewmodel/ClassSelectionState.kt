package com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.core.model.NetworkUIState
import com.tendebit.dungeonmaster.core.model.SelectionState
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class ClassSelectionState(private val supplier: CharacterClassInfoSupplier) : SelectionState<CharacterClassDirectory, CharacterClassInfo>, NetworkUIState {
    private var job: Job? = null

    private val stateSubject = BehaviorSubject.create<ClassSelectionState>()
    val changes = stateSubject as Observable<ClassSelectionState>
    override val options = ArrayList<CharacterClassDirectory>()
    override var selection: CharacterClassInfo? = null
    override var activeNetworkCalls = 0

    init {
        loadClassOptions()
    }

    override fun updateOptions(options: List<CharacterClassDirectory>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun select(option: CharacterClassDirectory) {
        if (option.primaryId() != selection?.primaryId()) {
            job = launch(UI) {
                try {
                    onNetworkCallStart()
                    notifyDataChanged()
                    val result = async(parent = job) {
                        supplier.getClassInfo(option)
                    }.await()
                    Log.d("CHARACTER_CREATION", result.toString())
                    selection = result
                } catch (e: Exception) {
                    Log.e("CHARACTER_CREATION", "Got an error", e)
                } finally {
                    onNetworkCallFinish()
                    notifyDataChanged()
                }
            }
        }
    }

    override fun cancelAllCalls() {
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun loadClassOptions() {
        job = launch(UI) {
            try {
                onNetworkCallStart()
                notifyDataChanged()
                val result = async(parent = job) {  supplier.getCharacterClasses() }.await()
                Log.d("CHARACTER_CREATION", "Got " + result.characterClassDirectories.size + " character classes. The first one is " + result.characterClassDirectories[0].name)
                updateOptions(result.characterClassDirectories)
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error", e)
            } finally {
                onNetworkCallFinish()
                notifyDataChanged()
            }
        }
    }

    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }

}