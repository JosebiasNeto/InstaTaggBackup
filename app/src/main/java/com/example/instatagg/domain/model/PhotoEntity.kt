package com.example.instatagg.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var path: String?,
    var name: String?,
    var color: String?
        )