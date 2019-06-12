package com.tendebit.dungeonmaster.charactercreation3.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.StoredCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.StoredCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.StoredClassDao
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.StoredSelectionCharacterClassJoin

@Database(entities = [StoredCharacterClass::class, StoredCharacterClassSelection::class, StoredSelectionCharacterClassJoin::class], version = 1, exportSchema = false)
abstract class CharacterCreationDb : RoomDatabase() {

	abstract fun classDao(): StoredClassDao

	companion object {
		private var INSTANCE: CharacterCreationDb? = null

		fun getInstance(context: Context) : CharacterCreationDb {
			if (INSTANCE == null) {
				synchronized(CharacterCreationDb::class) {
					INSTANCE = Room.databaseBuilder(context.applicationContext,
							CharacterCreationDb::class.java, "dnd_character_creation.db")
							.build()
				}
			}
			return INSTANCE!!
		}
	}

}
