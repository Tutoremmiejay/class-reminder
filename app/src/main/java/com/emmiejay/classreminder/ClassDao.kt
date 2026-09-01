package com.emmiejay.classreminder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {

    @Query("SELECT * FROM classes ORDER BY dayOfWeek, hour, minute")
    fun getAll(): Flow<List<ClassEntry>>

    @Query("SELECT * FROM classes WHERE id = :id")
    suspend fun getById(id: Int): ClassEntry?

    @Query("SELECT * FROM classes")
    suspend fun getAllOnce(): List<ClassEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ClassEntry): Long

    @Update
    suspend fun update(entry: ClassEntry)

    @Delete
    suspend fun delete(entry: ClassEntry)

    @Query("SELECT COUNT(*) FROM classes")
    suspend fun count(): Int
}
