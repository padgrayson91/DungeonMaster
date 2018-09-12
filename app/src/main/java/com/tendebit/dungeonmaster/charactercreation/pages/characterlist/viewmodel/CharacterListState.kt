package com.tendebit.dungeonmaster.charactercreation.pages.characterlist.viewmodel

import com.tendebit.dungeonmaster.core.model.SelectionState
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import io.reactivex.subjects.BehaviorSubject

class CharacterListState : SelectionState<StoredCharacter, StoredCharacter> {

    override val options = ArrayList<StoredCharacter>()
    override var selection: StoredCharacter? = null
    val changes = BehaviorSubject.create<CharacterListState>()
    var isNewCharacter = false

    override fun updateOptions(options: List<StoredCharacter>) {
        this.options.clear()
        this.options.addAll(options)
        notifyDataChanged()
    }

    override fun select(option: StoredCharacter) {
        selection = option
        notifyDataChanged()
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
}