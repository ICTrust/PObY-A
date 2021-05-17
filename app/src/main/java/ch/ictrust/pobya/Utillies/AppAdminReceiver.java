package ch.ictrust.pobya.Utillies;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserHandle;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import ch.ictrust.pobya.activity.MainActivity;



public class AppAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "AppAdminReceiver";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        // Enable the profile
        DevicePolicyManager manager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = getComponentName(context);
        manager.setProfileName(componentName, "PObY-A");

        // Open the main screen
        Intent launch = new Intent(context, MainActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }

    /**
     * @return A newly instantiated {@link ComponentName} for this
     * DeviceAdminReceiver.
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), AppAdminReceiver.class);
    }



    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin : enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin : disabled", Toast.LENGTH_SHORT).show();
    }


    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Warning: Device Admin is going to be disabled.";
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent, UserHandle user){
    }

    @Override
    public void onSecurityLogsAvailable(Context context, Intent intent) {
        super.onSecurityLogsAvailable(context, intent);
    }
}

