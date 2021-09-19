package com.example.instatagg.data.dbTaggs

import com.example.instatagg.domain.model.Tagg

class TaggsDBDataSource(
    private val taggsDao: TaggsDao
){
    fun insertTagg(tagg: Tagg) {
        taggsDao.insertTagg(tagg)
    }

    fun getTaggs(): List<Tagg> {
        return taggsDao.getTaggs()
    }

    fun changeTaggName(name: String, newTagg: String) {
        taggsDao.changeTaggName(name, newTagg)
    }

    fun delTagg(id: Int) {
        taggsDao.delTagg(id)
    }

    fun clearTaggs() {
        taggsDao.clearTaggs()
    }
}