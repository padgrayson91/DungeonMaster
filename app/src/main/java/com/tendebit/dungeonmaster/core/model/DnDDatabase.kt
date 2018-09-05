package com.tendebit.dungeonmaster.core.model


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StoredResponse::class], version = 1, exportSchema = false)
abstract class DnDDatabase : RoomDatabase() {
    abstract fun responseDao() : StoredResponseDao

    companion object {
        private var INSTANCE: DnDDatabase? = null

        fun getInstance(context: Context) : DnDDatabase {
            if (INSTANCE == null) {
                synchronized(DnDDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            DnDDatabase::class.java, "dnd5eresponses.db")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }

}