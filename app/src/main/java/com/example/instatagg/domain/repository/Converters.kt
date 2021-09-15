package com.example.instatagg.domain.repository

import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.model.Tagg

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