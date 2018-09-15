package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.core.model.NetworkUIState
import com.tendebit.dungeonmaster.core.viewmodel.SelectionViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class ClassSelectionViewModel(private val supplier: CharacterClassInfoSupplier) : SelectionViewModel<CharacterClassDirectory, CharacterClassInfo>, NetworkUIState {
    private var job: Job? = null

    val optionsSubject = BehaviorSubject.create<List<CharacterClassDirectory>>()
    override val options = optionsSubject.toFlowable(BackpressureStrategy.DROP)
    override val selection = BehaviorSubject.create<CharacterClassInfo>()
    private var previousSelection: CharacterClassInfo? = null
    override var activeNetworkCalls = 0
    override val networkCallChanges = PublishSubject.create<Int>()
    init {
        loadClassOptions()
    }

    private fun updateOptions(update: List<CharacterClassDirectory>) {
        this.optionsSubject.onNext(update)
    }

    override fun select(option: CharacterClassDirectory) {

        if (option.primaryId() != previousSelection?.primaryId()) {
            job = launch(UI) {
                try {
                    onNetworkCallStart()
                    val result = async(parent = job) {
                        supplier.getClassInfo(option)
                    }.await()
                    previousSelection = result
                    selection.onNext(result)
                } catch (e: Exception) {
                    Log.e("CHARACTER_CREATION", "Got an error", e)
                } finally {
                    onNetworkCallFinish()
                }
            }
        } else {
            selection.onNext(previousSelection!!)
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
                val result = async(parent = job) {  supplier.getCharacterClasses() }.await()
                Log.d("CHARACTER_CREATION", "Got " + result.characterClassDirectories.size + " character classes. The first one is " + result.characterClassDirectories[0].name)
                updateOptions(result.characterClassDirectories)
            } catch (e: Exception) {
                Log.e("CHARACTER_CREATION", "Got an error", e)
            } finally {
                onNetworkCallFinish()
            }
        }
    }

}