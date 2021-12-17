package com.jnsoft.instatagg.domain.repository

import com.jnsoft.instatagg.data.database.PhotosDao
import com.jnsoft.instatagg.data.database.TaggsDao
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg

class MainRepository(
    private val photosDao: PhotosDao,
    private val taggsDao: TaggsDao
) {
    suspend fun insertPhoto(photo: Photo) {
        photosDao.insert(Converters.toPhotoEntity(photo))
    }

    suspend fun getPhotos(id: Long): List<Photo> {
        return photosDao.getPhotos(id).map {
            Converters.toPhoto(it)
        }
    }

    suspend fun delPhoto(id: Long) {
        photosDao.delPhoto(id)
    }

    suspend fun clearTagg(id: Long) {
        photosDao.clearTagg(id)
    }

    suspend fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long) {
        photosDao.movePhoto(newTaggName, newTaggColor, newTaggId, id)
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
    suspend fun changeTaggColor(id: Long, newColor: Int){
        taggsDao.changeTaggColor(id, newColor)
    }
    suspend fun getTagg(id: Long): Tagg{
        return taggsDao.getTagg(id)
    }

}