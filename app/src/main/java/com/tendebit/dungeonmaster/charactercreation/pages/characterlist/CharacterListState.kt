package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.SelectionState
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class CharacterListState(private val db : DnDDatabase) : SelectionState<StoredCharacter, StoredCharacter> {

    override val options = BehaviorSubject.create<List<StoredCharacter>>()
    override val selection = BehaviorSubject.create<StoredCharacter>()
    var job: Job? = null
    val changes = BehaviorSubject.create<CharacterListState>()
    val newCharacterCreationStart = PublishSubject.create<Any>()
    var dbDisposable: Disposable? = null

    init {
        attemptLoadSavedCharactersFromDb()
    }

    override fun updateOptions(options: List<StoredCharacter>) {
        this.options.onNext(options)
    }

    override fun select(option: StoredCharacter) {
        selection.onNext(option)
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
        job = launch(UI) {
            dbDisposable = async(parent = job) {
                db.characterDao().getCharacters().subscribe { updateOptions(it) }
            }.await()
        }
    }
}