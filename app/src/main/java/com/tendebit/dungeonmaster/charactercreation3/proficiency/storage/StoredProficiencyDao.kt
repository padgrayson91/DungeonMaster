package com.tendebit.dungeonmaster.charactercreation3.proficiency.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoredProficiencyDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun storeProficiencyInfo(info: StoredProficiency)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun storeProficiencyGroup(group: StoredProficiencyGroup)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeProficiencyGroupJoin(join: StoredProficiencyGroupJoin)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeProficiencySelection(selection: StoredProficiencySelection)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeProficiencySelectionJoin(join: StoredProficiencySelectionJoin)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun storeProficiencySelectionToProficiencyJoin(join: StoredProficiencySelectionToProficiencyJoin)

	@Query("""SELECT id, choiceCount FROM proficiency_groups INNER JOIN proficiency_selection_join ON proficiency_groups.id=proficiency_selection_join.groupId
		WHERE proficiency_selection_join.selectionId=:selectionId ORDER BY proficiency_selection_join.sortOrder ASC""")
	fun getGroupsForSelection(selectionId: String): List<StoredProficiencyGroup>

	@Query( """SELECT id, proficiency_name FROM proficiencies INNER JOIN proficiency_group_join ON proficiencies.id=proficiency_group_join.proficiencyId
		WHERE proficiency_group_join.groupId=:groupId""")
	fun getProficienciesForGroup(groupId: String): List<StoredProficiency>

	@Query("""SELECT id, proficiency_name FROM proficiencies INNER JOIN proficiency_selection_to_proficiency_join ON proficiencies.id=proficiency_selection_to_proficiency_join.proficiencyId
		WHERE proficiency_selection_to_proficiency_join.selectionId=:selectionId AND proficiency_selection_to_proficiency_join.selectionState=1
		AND proficiency_selection_to_proficiency_join.groupId=:groupId""")
	fun getSelectedProficienciesInGroup(groupId: String, selectionId: String): List<StoredProficiency>

}