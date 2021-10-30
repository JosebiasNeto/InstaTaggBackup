package com.example.instatagg.data.dbPhotos

import com.example.instatagg.domain.model.PhotoEntity

class PhotosDBDataSource (
    private val photosDao: PhotosDao
        ){
    suspend fun insertPhoto(photoEntity: PhotoEntity) {
        photosDao.insert(photoEntity)
    }

    suspend fun getPhotos(name: String): List<PhotoEntity> {
        return photosDao.getPhotos(name)
    }

    suspend fun changeTagg(newTaggName: String, newTaggColor: Int, newTaggId: Long, currentTaggId: Long) {
        photosDao.changeTagg(newTaggName, newTaggColor, newTaggId, currentTaggId)
    }

    suspend fun delPhoto(id: Int) {
        photosDao.delPhoto(id)
    }

    suspend fun clearTagg(name: String) {
        photosDao.clearTagg(name)
    }

    suspend fun movePhoto(name: String, id: Int) {
        photosDao.movePhoto(name, id)
    }

    suspend fun importPhoto(path: String, name: String, color: String) {
        photosDao.importPhoto(path, name, color)
    }
}