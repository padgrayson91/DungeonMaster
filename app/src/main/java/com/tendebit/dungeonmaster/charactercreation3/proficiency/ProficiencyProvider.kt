package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import io.reactivex.Observable

interface ProficiencyProvider {

	val proficiencyOptions: Observable<ItemState<out DndProficiencySelection>>

}
