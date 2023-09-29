package ch.ictrust.pobya.clam.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.ictrust.pobya.clam.models.HSB

@Dao
interface HSBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hsb: HSB)

    @Query("SELECT * FROM hsb ORDER BY hashString ASC")
    fun getAll(): LiveData<List<HSB>>

    @Query("SELECT * FROM hsb WHERE hashString=(:hash)")
    fun getByHash(hash: String): HSB

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAll(hsb: List<HSB>)

}