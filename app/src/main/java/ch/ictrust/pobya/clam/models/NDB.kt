package ch.ictrust.pobya.clam.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "ndb")
data class NDB (
    // MalwareName:TargetType:Offset:HexSignature[:min_flevel:[max_flevel]]
    var malwareName: String,
    var targetType: Long,
    var offset: String,
    var hexSignature: Int,
    var minLevel: Int,
    var maxLevel: Int

){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}