package com.example.instatagg.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.instatagg.domain.model.PhotoEntity

@Dao
interface PhotosDao {
    @Insert
    fun insert(photoEntity: PhotoEntity)

    @Query("SELECT name FROM photos")
    fun getPhotos(name: String): List<PhotoEntity>

    @Query("UPDATE photos SET name = :newTagg WHERE name = :name")
    fun changeTagg(name: String, newTagg: String)

    @Query("DELETE FROM photos WHERE id")
    fun delPhoto(id: Int)

    @Query("DELETE FROM photos WHERE name")
    fun clearTagg(name: String)

    @Query("UPDATE photos SET name = :name WHERE id = :id")
    fun movePhoto(name: String, id: Int)

    @Query("INSERT INTO photos (path, name, color) VALUES (:path, :name, :color)")
    fun importPhoto(path: String, name: String, color: String)
}