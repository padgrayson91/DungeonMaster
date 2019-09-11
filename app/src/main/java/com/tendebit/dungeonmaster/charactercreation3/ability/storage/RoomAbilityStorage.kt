package com.tendebit.dungeonmaster.charactercreation3.ability.storage

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.logger
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.storage.DndAbilityStorage
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject

class RoomAbilityStorage(private val dao: StoredAbilityDao, private val concurrency: Concurrency) : DndAbilityStorage {

	override fun storeAbilityBonuses(bonuses: Array<DndAbilityBonus>, sourceId: CharSequence) {
		logger.writeDebug("Got request to store bonuses for $sourceId")
		concurrency.runDiskOrNetwork({
			dao.storeAbilityBonuses(StoredDndAbilityBonus.fromAbilityBonuses(bonuses, sourceId))
		})
	}

	override fun findAbilityBonuses(sourceId: CharSequence): Maybe<Array<DndAbilityBonus>> {
		val subject = MaybeSubject.create<Array<DndAbilityBonus>>()
		logger.writeDebug("Got request to find abilities for $sourceId in storage")
		concurrency.runDiskOrNetwork({
			val storedArray = dao.getAbilityBonuses(sourceId) ?: subject.onComplete()
			subject.onSuccess(storedArray.toAbilityBonusArray())
		})

		return subject
	}

}
