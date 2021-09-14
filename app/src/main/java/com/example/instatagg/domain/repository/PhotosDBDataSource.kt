package com.example.instatagg.domain.repository

import com.example.instatagg.data.db.PhotosDao
import com.example.instatagg.domain.model.PhotoEntity

class PhotosDBDataSource (
    private val photosDao: PhotosDao
        ) : PhotosDao{
    override fun insert(photoEntity: PhotoEntity) {
        photosDao.insert(photoEntity)
    }

    override fun getPhotos(name: String): List<PhotoEntity> {
        return photosDao.getPhotos(name)
    }

    override fun changeTagg(name: String, newTagg: String) {
        photosDao.changeTagg(name, newTagg)
    }

    override fun delPhoto(id: Int) {
        photosDao.delPhoto(id)
    }

    override fun clearTagg(name: String) {
        photosDao.clearTagg(name)
    }

    override fun movePhoto(name: String, id: Int) {
        photosDao.movePhoto(name, id)
    }

    override fun importPhoto(path: String, name: String, color: String) {
        photosDao.importPhoto(path, name, color)
    }
}