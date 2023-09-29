package ch.ictrust.pobya.clam

import android.app.Application
import android.content.Context
import ch.ictrust.pobya.clam.models.HSB
import ch.ictrust.pobya.clam.repositroy.HSBRepository
import ch.ictrust.pobya.utillies.ArchiveHelper
import ch.ictrust.pobya.utillies.HashUtils
import ch.ictrust.pobya.utillies.Utilities
import java.io.File
import java.security.MessageDigest

class ClamManager {

    companion object {
        private var instance: ClamManager? = null
        private var context: Context? = null
        var entriesHSB = ArrayList<HSB>()

        @Synchronized
        fun getInstance(c: Context): ClamManager {
            if (instance == null) {
                context = c
                entriesHSB  = ArrayList()
                instance = ClamManager()
            }
            return instance as ClamManager
        }
    }

    fun checkHSB(filePath: String, appName : String): Boolean {
        // TODO: Handle exception (filePath not exist, access denied?)
        val extractPath = context?.getExternalFilesDir(null)?.path +
                            File.separator.toString() + "PObY-A" + File.separator.toString()
        val extractDir = File(extractPath)
        val mDigestSHA256 =  MessageDigest.getInstance("sha256")
        val mDigestMD5 = MessageDigest.getInstance("md5")

        if (!extractDir.exists())
            extractDir.mkdirs()

        ArchiveHelper().unpackAPK(extractPath + appName + File.separator.toString(), filePath)


        val folder = File(extractPath + appName + File.separator.toString() +
                appName + File.separator.toString() )

        folder.walkTopDown().forEach {
            if (!it.isDirectory ) {
                val md5 = HashUtils.getCheckSumFromFilePath(mDigestMD5, it.path)
                val sha256 = HashUtils.getCheckSumFromFilePath(mDigestSHA256, it.path)

                // Get HSB entry using hash (md5 or sha256)
                // For performance optimizations, in case MD5 is found, no need to check the SHA256
                // Does it make sense?
                val resultMd5 = HSBRepository.getInstance(context?.applicationContext as Application).getByHash(md5)
                if(resultMd5 != null){
                    Utilities.deleteRecursive(folder)
                    return true
                }

                val resultSha256 = HSBRepository.getInstance(context?.applicationContext as Application).getByHash(sha256)
                if(resultSha256!= null) {
                    Utilities.deleteRecursive(folder)
                    return true
                }
            }
        }
        Utilities.deleteRecursive(folder)
        return false
    }

}