package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.TAG
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
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
 * ViewModel for character class selection. Exposes functionality to read the list of options and make a selection
 */
class ClassSelectionViewModel(private val supplier: CharacterClassInfoSupplier) : SelectionViewModel<CharacterClassDirectory, CharacterClassInfo>, AsyncViewModel {
    private var job: Job? = null

    private val optionsSubject = BehaviorSubject.create<List<CharacterClassDirectory>>()
    override val options = optionsSubject.toFlowable(BackpressureStrategy.DROP)!!
    override val selection = BehaviorSubject.create<CharacterClassInfo>()
    private var previousSelection: CharacterClassInfo? = null
    override var activeAsyncCalls = 0
    override val asyncCallChanges = PublishSubject.create<Int>()
    init {
        loadClassOptions()
    }

    override fun performActions(target: CharacterClassDirectory, actions: List<ItemAction>) {
        for (action in actions) {
            when(action) {
                ItemAction.HIGHLIGHT -> Log.d(TAG, "Highlighted class")
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

    private fun updateOptions(update: List<CharacterClassDirectory>) {
        this.optionsSubject.onNext(update)
    }

    private fun select(option: CharacterClassDirectory) {

        if (option.primaryId() != previousSelection?.primaryId()) {
            job = launch(UI) {
                try {
                    onAsyncCallStart()
                    val result = async(parent = job) {
                        supplier.getClassInfo(option)
                    }.await()
                    previousSelection = result
                    selection.onNext(result)
                } catch (e: Exception) {
                    Log.e(TAG, "Got an error", e)
                } finally {
                    onAsyncCallFinish()
                }
            }
        } else {
            selection.onNext(previousSelection!!)
        }
    }

    private fun loadClassOptions() {
        job = launch(UI) {
            try {
                onAsyncCallStart()
                val result = async(parent = job) {  supplier.getCharacterClasses() }.await()
                updateOptions(result.characterClassDirectories)
            } catch (e: Exception) {
                Log.e(TAG, "Got an error", e)
            } finally {
                onAsyncCallFinish()
            }
        }
    }

}