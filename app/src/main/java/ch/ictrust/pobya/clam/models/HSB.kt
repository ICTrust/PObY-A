package ch.ictrust.pobya.clam.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName= "hsb", indices =[
        Index(
                value = ["hashString"],
                unique = true
        )]
)
data class HSB (
        // HashString:FileSize:MalwareName[:FuncLevelSpec]
        // TODO: add hash string type: MD5 or SHA256 => (optimize performance for SQl queries)
        var hashString: String,
        var fileSize: Long,
        var malwareName: String,
        var funcLevelSpec: Int,
) {
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}

