package com.example.instatagg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.instatagg.domain.model.PhotoEntity

@Dao
interface PhotosDao {
    @Insert
    fun insert(photoEntity: PhotoEntity)

    @Query("SELECT * FROM photos WHERE name = :name")
    fun getPhotos(name: String): List<PhotoEntity>

    @Query("UPDATE photos SET name = :newTaggName, color = :newTaggColor, taggid = :newTaggId WHERE taggid = :currentTaggId")
    fun changeTagg(newTaggName: String, newTaggColor: Int, newTaggId: Long, currentTaggId: Long)

    @Query("DELETE FROM photos WHERE id = :id")
    fun delPhoto(id: Int)

    @Query("DELETE FROM photos WHERE name = :name")
    fun clearTagg(name: String)

    @Query("UPDATE photos SET name = :name WHERE id = :id")
    fun movePhoto(name: String, id: Int)

    @Query("INSERT INTO photos (path, name, color) VALUES (:path, :name, :color)")
    fun importPhoto(path: String, name: String, color: String)
}