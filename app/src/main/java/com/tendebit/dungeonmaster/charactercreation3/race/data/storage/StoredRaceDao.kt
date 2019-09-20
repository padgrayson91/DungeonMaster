package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoredRaceDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeRaceInfo(info: StoredRace)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeRaceSelection(selection: StoredRaceSelection)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeRaceSelectionJoin(join: StoredRaceSelectionJoin)

	@Query("""SELECT id,race_name FROM races INNER JOIN race_selection_join ON races.id=race_selection_join.raceId
		WHERE race_selection_join.selectionId=:selectionId""")
	fun getRacesForSelection(selectionId: String): List<StoredRace>

	@Query("""SELECT id,race_name FROM races INNER JOIN race_selection_join ON races.id=race_selection_join.raceId
		WHERE race_selection_join.selectionId=:selectionId AND race_selection_join.selectionState=1""")
	fun getSelectedRaceForSelection(selectionId: String): StoredRace?

}
