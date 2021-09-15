package com.example.instatagg.data.dbTaggs

import com.example.instatagg.domain.model.Tagg

class TaggsDBDataSource(
    private val taggsDao: TaggsDao
){
    fun insert(tagg: Tagg) {
        taggsDao.insert(tagg)
    }

    fun getTaggs(): List<Tagg> {
        return taggsDao.getTaggs()
    }

    fun changeName(name: String, newTagg: String) {
        taggsDao.changeName(name, newTagg)
    }

    fun delTagg(id: Int) {
        taggsDao.delTagg(id)
    }

    fun clearTaggs() {
        taggsDao.clearTaggs()
    }
}