package ch.ictrust.pobya.Utillies

import com.google.firebase.database.*

class FirebaseHelper {
    companion object {
        var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
            getReference("packages").keepSynced(true)
        }
        var db: DatabaseReference = mDatabase.reference
    }

    fun getDatabaseReference(): DatabaseReference {
        return db
    }

    fun getDatabase(): FirebaseDatabase {
        return mDatabase
    }

}