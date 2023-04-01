package ch.ictrust.pobya.models

import androidx.room.Entity

@Entity(primaryKeys = ["packageName", "permission"])
data class ApplicationPermissionCrossRef(
    val packageName: String,
    val permission: String
)