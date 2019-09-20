package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface StoredClassDao {

	@Insert(onConflict = REPLACE)
	fun storeClassInfo(info: StoredCharacterClass)

	@Query("""SELECT * FROM classes WHERE id=:id""")
	fun getClassInfo(id: String): StoredCharacterClass?

	@Insert(onConflict = REPLACE)
	fun storeClassSelection(selection: StoredCharacterClassSelection)

	@Insert(onConflict = REPLACE)
	fun storeSelectionClassJoin(join: StoredSelectionCharacterClassJoin)

	@Query("""SELECT * FROM classes INNER JOIN class_selection_join ON classes.id=class_selection_join.classId
		WHERE class_selection_join.selectionId=:selectionId""")
	fun getClassesForSelection(selectionId: String): List<StoredCharacterClass>

	@Query("""SELECT * FROM classes INNER JOIN class_selection_join ON classes.id=class_selection_join.classId
		WHERE class_selection_join.selectionId=:selectionId AND class_selection_join.selectionState=1""")
	fun getSelectedClassForSelection(selectionId: String): StoredCharacterClass?

}