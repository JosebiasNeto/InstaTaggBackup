package com.jnsoft.instatagg.data.local

import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.domain.repository.Converters

class LocalDatabase(
    private val photosDao: PhotosDao,
    private val taggsDao: TaggsDao
) : LocalDataSource {

    override suspend fun insertPhoto(photo: Photo, size: Long) {
        photosDao.insert(Converters.toPhotoEntity(photo))
        photo.tagg!!.id?.let { increaseTaggSize(size, it) }
    }

    override suspend fun getPhotos(id: Long): List<Photo> {
        return photosDao.getPhotos(id).map {
            Converters.toPhoto(it)
        }
    }

    override suspend fun delPhoto(photo: Photo, size: Long) {
        photo.id?.let { photosDao.delPhoto(it) }
        photo.tagg!!.id?.let{decreaseTaggSize(size, it)}
    }

    override suspend fun clearTagg(id: Long) {
        photosDao.clearTagg(id)
    }

    override suspend fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long,
                          id: Long, size: Long, oldTaggId: Long) {
        photosDao.movePhoto(newTaggName, newTaggColor, newTaggId, id)
        decreaseTaggSize(size, oldTaggId)
        increaseTaggSize(size, newTaggId)
    }

    override suspend fun importPhoto(path: String, name: String, color: String) {
        photosDao.importPhoto(path, name, color)
    }

    override suspend fun insertTagg(tagg: Tagg){
        taggsDao.insertTagg(tagg)
    }

    override suspend fun getTaggs(): List<Tagg> {
        return taggsDao.getTaggs()
    }

    override suspend fun changeTaggName(id: Long, newTagg: String){
        taggsDao.changeTaggName(id, newTagg)
    }

    override suspend fun delTagg(id: Long){
        taggsDao.delTagg(id)
    }

    override suspend fun changeTaggColor(id: Long, newColor: Int){
        taggsDao.changeTaggColor(id, newColor)
    }

    override suspend fun getTagg(id: Long): Tagg {
        return taggsDao.getTagg(id)
    }

    override suspend fun increaseTaggSize(size: Long, taggId: Long){
        taggsDao.increaseTaggSize(size, taggId)
    }

    override suspend fun decreaseTaggSize(size: Long, taggId: Long){
        taggsDao.decreaseTaggSize(size, taggId)
    }
}