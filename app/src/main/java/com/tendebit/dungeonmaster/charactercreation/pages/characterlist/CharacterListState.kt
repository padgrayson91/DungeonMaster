package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.SelectionState
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class CharacterListState(private val db : DnDDatabase) : SelectionState<StoredCharacter, StoredCharacter> {

    override val options = ArrayList<StoredCharacter>()
    override var selection: StoredCharacter? = null
    var job: Job? = null
    val changes = BehaviorSubject.create<CharacterListState>()
    var isNewCharacter = false
    var dbDisposable: Disposable? = null

    init {
        attemptLoadSavedCharactersFromDb()
    }

    override fun updateOptions(options: List<StoredCharacter>) {
        this.options.clear()
        this.options.addAll(options)
        notifyDataChanged()
    }

    override fun select(option: StoredCharacter) {
        selection = option
        notifyDataChanged()
    }

    fun cancelAllCalls() {
        launch(UI) {
            job?.cancelAndJoin()
        }
        dbDisposable?.dispose()
    }

    fun createNewCharacter() {
        selection = null
        isNewCharacter = true
        notifyDataChanged()
        isNewCharacter = false
    }

    private fun notifyDataChanged() {
        changes.onNext(this)
    }

    private fun attemptLoadSavedCharactersFromDb()  {
        job = launch(UI) {
            dbDisposable = async(parent = job) {
                db.characterDao().getCharacters().subscribe { updateOptions(it) }
            }.await()
        }
    }
}