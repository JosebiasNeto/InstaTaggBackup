package com.jnsoft.instatagg.domain.repository

import androidx.activity.result.ActivityResult
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg

interface MainRepository{

    suspend fun insertPhoto(photo: Photo)

    suspend fun getPhotos(id: Long): List<Photo>

    suspend fun delPhoto(photo: Photo)

    suspend fun clearTagg(id: Long)

    suspend fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long,
                          id: Long, size: Long, oldTaggId: Long)

    suspend fun shareFiles(files: List<String>)

    suspend fun importFiles(result: ActivityResult, tagg: Tagg)

    suspend fun insertTagg(tagg: Tagg)

    suspend fun getTaggs(): List<Tagg>

    suspend fun changeTaggName(id: Long, newTagg: String)

    suspend fun delTagg(id: Long)

    suspend fun changeTaggColor(id: Long, newColor: Int)

    suspend fun getTagg(id: Long): Tagg

    suspend fun increaseTaggSize(size: Long, taggId: Long)

    suspend fun decreaseTaggSize(size: Long, taggId: Long)
}