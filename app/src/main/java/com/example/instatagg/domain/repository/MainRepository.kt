package com.example.instatagg.domain.repository

import com.example.instatagg.data.dbPhotos.PhotosDBDataSource
import com.example.instatagg.data.dbTaggs.TaggsDBDataSource
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.model.Tagg

class MainRepository(
    private val photosDb: PhotosDBDataSource,
    private val taggsDb: TaggsDBDataSource
) {
    suspend fun insertPhoto(photoEntity: PhotoEntity) {
        photosDb.insertPhoto(photoEntity)
    }

    suspend fun getPhotos(name: String): List<PhotoEntity> {
        return photosDb.getPhotos(name)
    }

    suspend fun changeTagg(newTaggName: String, newTaggColor: Int, newTaggId: Long, currentTaggId: Long) {
        photosDb.changeTagg(newTaggName, newTaggColor, newTaggId, currentTaggId)
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
    suspend fun insertTagg(tagg: Tagg){
        taggsDb.insertTagg(tagg)
    }
    suspend fun getTaggs(): List<Tagg> {
        return taggsDb.getTaggs()
    }
    suspend fun changeTaggName(id: Long, newTagg: String){
        taggsDb.changeTaggName(id, newTagg)
    }
    suspend fun delTagg(id: Long){
        taggsDb.delTagg(id)
    }
    suspend fun clearTaggs(){
        taggsDb.clearTaggs()
    }
    suspend fun changeTaggColor(id: Long, newColor: Int){
        taggsDb.changeTaggColor(id, newColor)
    }

}