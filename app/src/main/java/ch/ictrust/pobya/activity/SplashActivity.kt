package ch.ictrust.pobya.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.ictrust.pobya.R
import ch.ictrust.pobya.Utillies.Prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        getHashKey(this)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        if (!Prefs.getInstance(this)!!.isFirstRun!!) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            val intent = Intent(this, WalkthroughActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun getHashKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }

    }
}
