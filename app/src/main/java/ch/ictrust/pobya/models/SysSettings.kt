package ch.ictrust.pobya.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SysSettings (
    @PrimaryKey
    var info: String,
    var testDescription: String,
    var currentValue: String,
    var expectedValue: String,
    var functionName: String,
    var action: String
)