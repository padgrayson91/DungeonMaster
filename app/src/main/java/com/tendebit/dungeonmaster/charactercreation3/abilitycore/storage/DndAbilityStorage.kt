package com.tendebit.dungeonmaster.charactercreation3.abilitycore.storage

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import io.reactivex.Maybe

interface DndAbilityStorage {

	fun storeAbilityBonuses(bonuses: Array<DndAbilityBonus>, sourceId: CharSequence)

	fun findAbilityBonuses(sourceId: CharSequence): Maybe<Array<DndAbilityBonus>>

}
