package com.example.instatagg.domain.repository

import com.example.instatagg.data.PhotosDao
import com.example.instatagg.data.TaggsDao
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.model.Tagg

class MainRepository(
    private val photosDao: PhotosDao,
    private val taggsDao: TaggsDao
) {
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
    suspend fun insertTagg(tagg: Tagg){
        taggsDao.insertTagg(tagg)
    }
    suspend fun getTaggs(): List<Tagg> {
        return taggsDao.getTaggs()
    }
    suspend fun changeTaggName(id: Long, newTagg: String){
        taggsDao.changeTaggName(id, newTagg)
    }
    suspend fun delTagg(id: Long){
        taggsDao.delTagg(id)
    }
    suspend fun clearTaggs(){
        taggsDao.clearTaggs()
    }
    suspend fun changeTaggColor(id: Long, newColor: Int){
        taggsDao.changeTaggColor(id, newColor)
    }

}