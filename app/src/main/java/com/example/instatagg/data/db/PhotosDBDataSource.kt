package com.example.instatagg.data.db

import com.example.instatagg.domain.model.PhotoEntity

class PhotosDBDataSource (
    private val photosDao: PhotosDao
        ){
    fun insert(photoEntity: PhotoEntity) {
        photosDao.insert(photoEntity)
    }

    fun getPhotos(name: String): List<PhotoEntity> {
        return photosDao.getPhotos(name)
    }

    fun changeTagg(name: String, newTagg: String) {
        photosDao.changeTagg(name, newTagg)
    }

    fun delPhoto(id: Int) {
        photosDao.delPhoto(id)
    }

    fun clearTagg(name: String) {
        photosDao.clearTagg(name)
    }

    fun movePhoto(name: String, id: Int) {
        photosDao.movePhoto(name, id)
    }

    fun importPhoto(path: String, name: String, color: String) {
        photosDao.importPhoto(path, name, color)
    }
}