package com.kevin.musicplayer.database.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.kevin.musicplayer.dao.TrackDao
import com.kevin.musicplayer.model.Track

@Database(entities = [Track::class], version = 1, exportSchema = false)
@TypeConverters(MusicTypeConverters::class)
abstract class MusicRoomDatabase : RoomDatabase() {

	companion object {
		@Volatile
		private var gameRoomDatabaseInstance: MusicRoomDatabase? = null

		fun getDatabase(context: Context): MusicRoomDatabase? {
			if (gameRoomDatabaseInstance == null) {
				synchronized(MusicRoomDatabase::class.java) {
					if (gameRoomDatabaseInstance == null) {
						gameRoomDatabaseInstance = Room.databaseBuilder(context.applicationContext,
								MusicRoomDatabase::class.java, "music_database")
								.build()
					}
				}
			}
			return gameRoomDatabaseInstance
		}
	}

	abstract fun trackDao(): TrackDao
}