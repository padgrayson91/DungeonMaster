package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.SelectionState
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class CharacterListViewModel(private val db : DnDDatabase) : SelectionState<StoredCharacter, StoredCharacter> {

    override lateinit var options: Flowable<List<StoredCharacter>>
    override val selection = BehaviorSubject.create<StoredCharacter>()
    private var job: Job? = null
    val newCharacterCreationStart = PublishSubject.create<Any>()
    private var dbDisposable: Disposable? = null

    init {
        attemptLoadSavedCharactersFromDb()
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
        options = db.characterDao().getCharacters()
    }
}