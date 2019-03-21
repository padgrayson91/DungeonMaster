package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import com.tendebit.dungeonmaster.charactercreation2.feature.StoredCharacterSupplier
import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import com.tendebit.dungeonmaster.core.viewmodel.SelectionViewModel
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the character list, which exposes functionality to read/update saved characters
 */
class CharacterListViewModel(private val supplier: StoredCharacterSupplier) :
        SelectionViewModel<DisplayedCharacter, DisplayedCharacter> {

    override lateinit var options: Flowable<List<DisplayedCharacter>>
    override val selection = PublishSubject.create<DisplayedCharacter>()
    val newCharacterCreationStart = PublishSubject.create<Any>()
    private val bgScope = CoroutineScope(Dispatchers.IO)

    init {
        loadCharacters()
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
        bgScope.launch { supplier.deleteCharacter(option.storedCharacter) }
    }

    fun createNewCharacter() {
        newCharacterCreationStart.onNext(Object())
    }

    private fun loadCharacters()  {
        options = supplier.getStoredCharacters()
                .flatMap { list ->
                    Flowable.fromIterable(list)
                            .map { DisplayedCharacter(it) }
                            .toList()
                            .toFlowable()
                }
    }
}