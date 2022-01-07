package com.jnsoft.instatagg.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "taggs")
data class Tagg (
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String,
    var color: Int,
    @ColumnInfo(defaultValue = "0")
    var size: Int
        ) : Parcelable