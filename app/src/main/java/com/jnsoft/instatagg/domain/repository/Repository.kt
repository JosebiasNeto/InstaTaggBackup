package com.jnsoft.instatagg.domain.repository

import androidx.activity.result.ActivityResult
import com.jnsoft.instatagg.data.local.LocalDataSource
import com.jnsoft.instatagg.data.remote.RemoteDataSource
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg

class Repository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): MainRepository {

    private val localFilesControl = LocalFilesControl()

    override suspend fun insertPhoto(photo: Photo) {
        localDataSource.insertPhoto(photo, localFilesControl.getFileSize(photo.path!!))
    }

    override suspend fun getPhotos(id: Long): List<Photo> {
        return localDataSource.getPhotos(id)
    }

    override suspend fun delPhoto(photo: Photo) {
        localDataSource.delPhoto(photo, localFilesControl.getFileSize(photo.path!!))
        localFilesControl.delFiles(photo.path!!)
    }

    override suspend fun clearTagg(id: Long) {
        localDataSource.clearTagg(id)
    }

    override suspend fun movePhoto(
        newTaggName: String, newTaggColor: Int, newTaggId: Long,
        id: Long, size: Long, oldTaggId: Long
    ) {
        localDataSource.movePhoto(newTaggName, newTaggColor, newTaggId, id, size, oldTaggId)
    }

    override suspend fun importFiles(result: ActivityResult, tagg: Tagg) {
        localFilesControl.getImportFiles(result).map { insertPhoto(Photo(it, tagg, null)) }
    }

    override suspend fun shareFiles(files: List<String>) {
        localFilesControl.shareFiles(files)
    }

    override suspend fun insertTagg(tagg: Tagg) {
        localDataSource.insertTagg(tagg)
    }

    override suspend fun getTaggs(): List<Tagg> {
        return localDataSource.getTaggs()
    }

    override suspend fun changeTaggName(id: Long, newTagg: String) {
        localDataSource.changeTaggName(id, newTagg)
    }

    override suspend fun delTagg(id: Long) {
        localDataSource.delTagg(id)
    }

    override suspend fun changeTaggColor(id: Long, newColor: Int) {
        localDataSource.changeTaggColor(id, newColor)
    }

    override suspend fun getTagg(id: Long): Tagg {
        return localDataSource.getTagg(id)
    }

    override suspend fun increaseTaggSize(size: Long, taggId: Long) {
        localDataSource.increaseTaggSize(size, taggId)
    }

    override suspend fun decreaseTaggSize(size: Long, taggId: Long) {
        localDataSource.decreaseTaggSize(size, taggId)
    }
}