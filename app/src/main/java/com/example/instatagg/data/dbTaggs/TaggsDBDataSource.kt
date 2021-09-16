package com.example.instatagg.data.dbTaggs

import com.example.instatagg.domain.model.Tagg

class TaggsDBDataSource(
    private val taggsDao: TaggsDao
){
    suspend fun insertTagg(tagg: Tagg) {
        taggsDao.insertTagg(tagg)
    }

    suspend fun getTaggs(): List<Tagg> {
        return taggsDao.getTaggs()
    }

    suspend fun changeTaggName(name: String, newTagg: String) {
        taggsDao.changeTaggName(name, newTagg)
    }

    suspend fun delTagg(id: Int) {
        taggsDao.delTagg(id)
    }

    suspend fun clearTaggs() {
        taggsDao.clearTaggs()
    }
}