package com.emmiejay.classreminder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ClassEntry::class], version = 1, exportSchema = false)
abstract class ClassDatabase : RoomDatabase() {
    abstract fun classDao(): ClassDao

    companion object {
        @Volatile
        private var INSTANCE: ClassDatabase? = null

        fun getInstance(context: Context): ClassDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClassDatabase::class.java,
                    "class_reminder.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        // Seed the database with the "Maths Made Simple by Emmiejay" weekly
        // schedule the first time the app runs. Safe to call repeatedly —
        // it checks the table is empty first.
        fun seedIfEmpty(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = getInstance(context)
                val dao = db.classDao()
                if (dao.count() > 0) return@launch

                val seed = listOf(
                    ClassEntry(dayOfWeek = Calendar.MONDAY, hour = 17, minute = 0, studentName = "Ethan"),
                    ClassEntry(dayOfWeek = Calendar.MONDAY, hour = 19, minute = 0, studentName = "Davian"),
                    ClassEntry(dayOfWeek = Calendar.TUESDAY, hour = 17, minute = 0, studentName = "Mary"),
                    ClassEntry(dayOfWeek = Calendar.WEDNESDAY, hour = 17, minute = 0, studentName = "Praise"),
                    ClassEntry(dayOfWeek = Calendar.WEDNESDAY, hour = 19, minute = 0, studentName = "Great"),
                    ClassEntry(dayOfWeek = Calendar.THURSDAY, hour = 17, minute = 30, studentName = "Jeremy"),
                    ClassEntry(dayOfWeek = Calendar.THURSDAY, hour = 19, minute = 0, studentName = "Davian"),
                    ClassEntry(dayOfWeek = Calendar.FRIDAY, hour = 17, minute = 0, studentName = "Amelia"),
                    ClassEntry(dayOfWeek = Calendar.FRIDAY, hour = 19, minute = 0, studentName = "Shiro"),
                    ClassEntry(dayOfWeek = Calendar.SATURDAY, hour = 9, minute = 0, studentName = "Amelia"),
                    ClassEntry(dayOfWeek = Calendar.SATURDAY, hour = 10, minute = 30, studentName = "Moplin"),
                    ClassEntry(dayOfWeek = Calendar.SATURDAY, hour = 15, minute = 30, studentName = "Sayo / Sope"),
                    ClassEntry(dayOfWeek = Calendar.SUNDAY, hour = 18, minute = 0, studentName = "Praise")
                )

                seed.forEach { entry ->
                    val id = dao.insert(entry)
                    AlarmScheduler.schedule(context, entry.copy(id = id.toInt()))
                }
            }
        }
    }
}
