package ch.ictrust.pobya.clam.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClamVersion(
    val version: Long,
    @PrimaryKey
    val dbType: ClamDbType,
    val buildTime: Long,
    val nbrSignatures: Long,
    val hashMD5: String,
    val digitalSignature: String,
    val updateDate: Long
)
