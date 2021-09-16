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

    @Query("UPDATE taggs SET name = :newTagg WHERE name = :name")
    fun changeTaggName(name: String, newTagg: String)

    @Query("DELETE FROM taggs WHERE id = :id")
    fun delTagg(id: Int)

    @Query("DELETE FROM taggs")
    fun clearTaggs()

}
