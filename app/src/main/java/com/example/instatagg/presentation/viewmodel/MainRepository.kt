package com.example.instatagg.presentation.viewmodel

import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.repository.PhotosDBDataSource

class MainRepository(
    private val photosDb: PhotosDBDataSource
) {
    suspend fun insert(photoEntity: PhotoEntity) {
        photosDb.insert(photoEntity)
    }

    suspend fun getPhotos(name: String): List<PhotoEntity> {
        return photosDb.getPhotos(name)
    }

    suspend fun changeTagg(name: String, newTagg: String) {
        photosDb.changeTagg(name, newTagg)
    }

    suspend fun delPhoto(id: Int) {
        photosDb.delPhoto(id)
    }

    suspend fun clearTagg(name: String) {
        photosDb.clearTagg(name)
    }

    suspend fun movePhoto(name: String, id: Int) {
        photosDb.movePhoto(name, id)
    }

    suspend fun importPhoto(path: String, name: String, color: String) {
        photosDb.importPhoto(path, name, color)
    }
}