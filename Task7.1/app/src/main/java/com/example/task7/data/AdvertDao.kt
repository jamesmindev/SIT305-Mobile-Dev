package com.example.task7.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.task7.model.Advert

@Dao
interface AdvertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvert(advert: Advert)

    @Update
    suspend fun updateAdvert(advert: Advert)

    @Delete
    suspend fun deleteAdvert(advert: Advert)

    @Query("SELECT * FROM adverts ORDER BY date DESC")
    fun getAllAdverts(): kotlinx.coroutines.flow.Flow<List<Advert>>

    @Query("SELECT * FROM adverts WHERE id = :advertId LIMIT 1")
    fun getAdvertById(advertId: Int): LiveData<Advert>
}