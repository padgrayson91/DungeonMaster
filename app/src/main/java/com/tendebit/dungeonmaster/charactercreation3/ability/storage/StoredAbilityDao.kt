package com.tendebit.dungeonmaster.charactercreation3.ability.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoredAbilityDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun storeAbilityBonuses(bonuses: StoredDndAbilityBonus)

	@Query("""SELECT * FROM ability_bonuses WHERE id=:sourceId""")
	fun getAbilityBonuses(sourceId: CharSequence): StoredDndAbilityBonus?

}
