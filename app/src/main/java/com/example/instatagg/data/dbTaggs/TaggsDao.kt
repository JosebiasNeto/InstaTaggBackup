package com.example.instatagg.data.dbTaggs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.instatagg.domain.model.Tagg

@Dao
interface TaggsDao {
    @Insert
    fun insertTagg(tagg: Tagg)

    @Query("SELECT * FROM taggs")
    fun getTaggs(): List<Tagg>

    @Query("UPDATE taggs SET name = :newTagg WHERE id = :id")
    fun changeTaggName(id: Long, newTagg: String)

    @Query("DELETE FROM taggs WHERE id = :id")
    fun delTagg(id: Long)

    @Query("DELETE FROM taggs")
    fun clearTaggs()

    @Query("UPDATE taggs SET color = :newColor WHERE id = :id")
    fun changeTaggColor(id: Long, newColor: Int)

}
