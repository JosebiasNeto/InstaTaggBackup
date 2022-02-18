package com.jnsoft.instatagg.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jnsoft.instatagg.domain.model.PhotoEntity

@Dao
interface PhotosDao {
    @Insert
    suspend fun insert(photoEntity: PhotoEntity)

    @Query("SELECT * FROM photos WHERE taggid = :id")
    suspend fun getPhotos(id: Long): List<PhotoEntity>

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun delPhoto(id: Long)

    @Query("DELETE FROM photos WHERE taggid = :id")
    suspend fun clearTagg(id: Long)

    @Query("UPDATE photos SET taggid = :newTaggId, name = :newTaggName, color = :newTaggColor WHERE id = :id")
    suspend fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long)

    @Query("INSERT INTO photos (path, name, color) VALUES (:path, :name, :color)")
    suspend fun importPhoto(path: String, name: String, color: String)
}