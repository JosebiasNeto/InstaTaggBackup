package com.example.instatagg.domain.repository

import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.model.Tagg

object Converters {
    fun toPhotoEntity(photo: Photo): PhotoEntity {
        return PhotoEntity(
            path = photo.path,
            name = photo.tagg.name,
            color = photo.tagg.color,
            id = null
        )
    }
    fun toPhoto(photoEntity: PhotoEntity): Photo{
        return Photo(
            path = photoEntity.path,
            tagg = Tagg(photoEntity.name, photoEntity.color),
            id = photoEntity.id
        )
    }
}