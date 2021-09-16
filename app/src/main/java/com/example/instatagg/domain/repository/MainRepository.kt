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
    suspend fun insertTagg(tagg: Tagg){
        taggsDb.insertTagg(tagg)
    }
    suspend fun getTaggs(): List<Tagg> {
        return taggsDb.getTaggs()
    }
    suspend fun changeTaggName(name: String, newTagg: String){
        taggsDb.changeTaggName(name, newTagg)
    }
    suspend fun delTagg(id: Int){
        taggsDb.delTagg(id)
    }
    suspend fun clearTaggs(){
        taggsDb.clearTaggs()
    }
}