package com.jnsoft.instatagg.domain.repository

import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.PhotoEntity
import com.jnsoft.instatagg.domain.model.Tagg

object Converters {
    fun toPhotoEntity(photo: Photo): PhotoEntity {
        return PhotoEntity(
            path = photo.path,
            name = photo.tagg?.name,
            color = photo.tagg?.color,
            id = null,
            taggid = photo.tagg?.id,
        )
    }
    fun toPhoto(photoEntity: PhotoEntity): Photo{
        return Photo(
            path = photoEntity.path,
            tagg = photoEntity.taggid?.let
            { photoEntity.name?.let { it1 -> photoEntity.color?.let { it2 ->
                Tagg(id = it, name = it1, color = it2) } } },
            id = photoEntity.id
        )
    }
}