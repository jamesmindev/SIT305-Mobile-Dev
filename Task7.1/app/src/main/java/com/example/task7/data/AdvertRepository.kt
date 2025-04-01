package com.example.task7.data

import androidx.lifecycle.LiveData
import com.example.task7.model.Advert

class AdvertRepository(private val advertDao: AdvertDao) {
    val allAdverts: kotlinx.coroutines.flow.Flow<List<Advert>> = advertDao.getAllAdverts()

    fun getAdvertById(advertId: Int): LiveData<Advert> {
        return advertDao.getAdvertById(advertId)
    }

    suspend fun insert(advert: Advert) {
        advertDao.insertAdvert(advert)
    }

    suspend fun update(advert: Advert) {
        advertDao.updateAdvert(advert)
    }

    suspend fun delete(advert: Advert) {
        advertDao.deleteAdvert(advert)
    }
}