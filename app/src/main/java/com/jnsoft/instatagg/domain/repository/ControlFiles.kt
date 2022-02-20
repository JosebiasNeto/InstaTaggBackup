package com.jnsoft.instatagg.domain.repository

import com.jnsoft.instatagg.presentation.SingleActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ControlFiles(): KoinComponent {

    private val singleActivity: SingleActivity by inject()

    private fun deleteFile(){

    }

}