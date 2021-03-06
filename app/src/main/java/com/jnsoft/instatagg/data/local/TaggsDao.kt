package com.jnsoft.instatagg.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jnsoft.instatagg.domain.model.Tagg

@Dao
interface TaggsDao {
    @Insert
    suspend fun insertTagg(tagg: Tagg)

    @Query("SELECT * FROM taggs")
    suspend fun getTaggs(): List<Tagg>

    @Query("UPDATE taggs SET name = :newTagg WHERE id = :id")
    suspend fun changeTaggName(id: Long, newTagg: String)

    @Query("DELETE FROM taggs WHERE id = :id")
    suspend fun delTagg(id: Long)

    @Query("DELETE FROM taggs")
    suspend fun clearTaggs()

    @Query("UPDATE taggs SET color = :newColor WHERE id = :id")
    suspend fun changeTaggColor(id: Long, newColor: Int)

    @Query("SELECT * FROM taggs WHERE id = :id")
    suspend fun getTagg(id: Long): Tagg

    @Query("UPDATE taggs SET size = size - :size WHERE id = :id")
    suspend fun decreaseTaggSize(size: Long, id: Long)

    @Query("UPDATE taggs SET size = size + :size WHERE id = :id")
    suspend fun increaseTaggSize(size: Long, id: Long)
}
