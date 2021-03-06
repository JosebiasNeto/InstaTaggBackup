package com.jnsoft.instatagg.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo (
    var path: String?,
    var tagg: Tagg?,
    var id: Long?,
    var checkboxVisibility : Boolean = false,
    var isChecked: Boolean = false
        ): Parcelable