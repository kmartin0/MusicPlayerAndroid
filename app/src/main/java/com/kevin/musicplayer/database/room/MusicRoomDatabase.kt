package com.kevin.musicplayer.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kevin.musicplayer.dao.LyricsDao
import com.kevin.musicplayer.model.Lyrics

@Database(entities = [Lyrics::class], version = 2, exportSchema = false)
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

	abstract fun lyricsDao(): LyricsDao

}