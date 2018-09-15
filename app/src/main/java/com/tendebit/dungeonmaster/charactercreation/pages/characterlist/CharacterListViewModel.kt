package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.viewmodel.DisplayedCharacter
import com.tendebit.dungeonmaster.core.viewmodel.SelectionViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class CharacterListViewModel(private val db : DnDDatabase) : SelectionViewModel<DisplayedCharacter, DisplayedCharacter> {

    override lateinit var options: Flowable<List<DisplayedCharacter>>
    override val selection = BehaviorSubject.create<DisplayedCharacter>()
    private var job: Job? = null
    val newCharacterCreationStart = PublishSubject.create<Any>()
    private var dbDisposable: Disposable? = null

    init {
        attemptLoadSavedCharactersFromDb()
    }

    override fun select(option: DisplayedCharacter) {
        selection.onNext(option)
    }

    fun delete(option: DisplayedCharacter) {
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