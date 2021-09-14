package com.example.instatagg.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
class PhotoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val path: String,
    val name: String,
    val color: String
        )