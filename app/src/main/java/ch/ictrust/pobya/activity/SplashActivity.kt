package ch.ictrust.pobya.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.ictrust.pobya.R
import ch.ictrust.pobya.utillies.Prefs
import ch.ictrust.pobya.utillies.Utilities
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        getHashKey(this)

        if (!Prefs.getInstance(this)!!.isFirstRun!!) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, WalkthroughActivity::class.java)
            Utilities.updateMalwareDB(this)
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