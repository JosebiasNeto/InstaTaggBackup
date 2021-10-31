package com.example.instatagg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.instatagg.domain.model.PhotoEntity

@Dao
interface PhotosDao {
    @Insert
    suspend fun insert(photoEntity: PhotoEntity)

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotos(id: Long): List<PhotoEntity>

    @Query("UPDATE photos SET name = :newTaggName, color = :newTaggColor, taggid = :newTaggId WHERE taggid = :currentTaggId")
    suspend fun changeTagg(newTaggName: String, newTaggColor: Int, newTaggId: Long, currentTaggId: Long)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun delPhoto(id: Int)

    @Query("DELETE FROM photos WHERE name = :name")
    suspend fun clearTagg(name: String)

    @Query("UPDATE photos SET name = :name WHERE id = :id")
    suspend fun movePhoto(name: String, id: Int)

    @Query("INSERT INTO photos (path, name, color) VALUES (:path, :name, :color)")
    suspend fun importPhoto(path: String, name: String, color: String)
}