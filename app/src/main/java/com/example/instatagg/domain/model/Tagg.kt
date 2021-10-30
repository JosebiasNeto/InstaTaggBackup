package com.example.instatagg.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "taggs")
data class Tagg (
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String,
    var color: Int
        ) : Serializable