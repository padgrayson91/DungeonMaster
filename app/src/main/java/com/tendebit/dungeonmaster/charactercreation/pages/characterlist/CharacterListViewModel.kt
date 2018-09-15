package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.viewmodel.DisplayedCharacter
import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import com.tendebit.dungeonmaster.core.viewmodel.SelectionViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

/**
 * ViewModel for the character list, which exposes functionality to read/update saved characters
 */
class CharacterListViewModel(private val db : DnDDatabase) : SelectionViewModel<DisplayedCharacter, DisplayedCharacter> {

    override lateinit var options: Flowable<List<DisplayedCharacter>>
    override val selection = BehaviorSubject.create<DisplayedCharacter>()
    private var job: Job? = null
    val newCharacterCreationStart = PublishSubject.create<Any>()
    private var dbDisposable: Disposable? = null

    init {
        attemptLoadSavedCharactersFromDb()
    }

    override fun performActions(target: DisplayedCharacter, actions: List<ItemAction>) {
        for (action in actions) {
            when (action) {
                ItemAction.SELECT -> select(target)
                ItemAction.DELETE -> delete(target)
                else -> throw RuntimeException(
                        "${CharacterListViewModel::class.java.simpleName} unable to perform action ${action.name}")
            }
        }
    }

    private fun select(option: DisplayedCharacter) {
        selection.onNext(option)
    }

    private fun delete(option: DisplayedCharacter) {
        launch { db.characterDao().deleteCharacter(option.storedCharacter) }
    }

    fun cancelAllCalls() {
        launch(UI) {
            job?.cancelAndJoin()
        }
        dbDisposable?.dispose()
    }

    fun createNewCharacter() {
        newCharacterCreationStart.onNext(Object())
    }

    private fun attemptLoadSavedCharactersFromDb()  {
        options = db.characterDao().getCharacters()
                .flatMap { list ->
                    Flowable.fromIterable(list)
                            .map { DisplayedCharacter(it) }
                            .toList()
                            .toFlowable()
                }
    }
}