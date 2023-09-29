package ch.ictrust.pobya.clam.dao

import androidx.room.*
import ch.ictrust.pobya.clam.models.ClamVersion

@Dao
interface ClamVersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clamVersion: ClamVersion)

    @Query("SELECT * FROM ClamVersion ORDER BY buildTime ASC limit 1")
    fun getLast(): ClamVersion

}