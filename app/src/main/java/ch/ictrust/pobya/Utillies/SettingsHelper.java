/*
 * This file is part of PObY-A.
 *
 * Copyright (C) 2023 ICTrust Sàrl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package ch.ictrust.pobya.utillies;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.autofill.AutofillManager;

import java.util.ArrayList;
import java.util.List;

import ch.ictrust.pobya.R;
import ch.ictrust.pobya.models.SysSettings;


public class SettingsHelper {

    private final String TAG = "SettingsHelper";
    private final Context context;
    private final ArrayList<SysSettings> sysSettings;
    private final DevicePolicyManager dpm;
    private final ComponentName compName;

    public SettingsHelper(Context context) {
        this.context = context;

        sysSettings = new ArrayList<>();
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(context, AppAdminReceiver.class);
    }


    public ArrayList<SysSettings> scan() {

        // screen lock enabled
        sysSettings.add(new SysSettings(context.getString(R.string.screen_lock_enabled_info), context.getString(R.string.screen_lock_enabled_desc), Boolean.valueOf(isScreenLockEnabled()).toString(), Boolean.TRUE.toString(), "screenLockSettings", context.getString(R.string.screen_lock_enabled_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.lock_screen_lock_after_timeout_info), context.getString(R.string.lock_screen_lock_after_timeout_desc), Boolean.valueOf(screenLockTimeoutEnabled()).toString(), Boolean.TRUE.toString(), "setScreenLockTimeoutEnabled", context.getString(R.string.lock_screen_lock_after_timeout_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.show_password_info), context.getString(R.string.show_password_desc), Boolean.valueOf(isPatternVisible()).toString(), Boolean.FALSE.toString(), "makePatternInvisible", context.getString(R.string.show_password_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.development_settings_enabled_info), context.getString(R.string.development_settings_enabled_desc), Boolean.valueOf(isPatternVisible()).toString(), Boolean.FALSE.toString(), "disableDevSetting", context.getString(R.string.development_settings_enabled_action)));

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) {
            sysSettings.add(new SysSettings("install_non_market_apps", "Install applications " + "that are not from play store", Boolean.valueOf(isInstallUnknownAppEnabled()).toString(), Boolean.FALSE.toString(), "disableInstallUnknownApp", "You will be redirected to development " + "Settings"));
        }

        sysSettings.add(new SysSettings(context.getString(R.string.auto_time_info), context.getString(R.string.auto_time_desc), Boolean.valueOf(isNetTimeEnabled()).toString(), Boolean.TRUE.toString(), "setNetTimeAuto", context.getString(R.string.auto_time_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.add_users_when_locked_info), context.getString(R.string.add_users_when_locked_desc), Boolean.valueOf(isAddUsersLockScreenEnabled()).toString(), Boolean.FALSE.toString(), "fixAddUsersLockScreenEnabled", context.getString(R.string.add_users_when_locked_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.lock_screen_show_notifications_info), context.getString(R.string.lock_screen_show_notifications_desc), Boolean.valueOf(isLockShowNotificationsEnabled()).toString(), Boolean.FALSE.toString(), "setLockShowNotifications", context.getString(R.string.lock_screen_show_notifications_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.location_enabled_info), context.getString(R.string.location_enabled_desc), Boolean.valueOf(isLocationEnabled()).toString(), Boolean.FALSE.toString(), "disableLocation", context.getString(R.string.location_enabled_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.voice_interaction_service_info), context.getString(R.string.voice_interaction_service_desc), Boolean.valueOf(isVoiceAssistanceEnabled()).toString(), Boolean.FALSE.toString(), "setVoiceAssistance", context.getString(R.string.voice_interaction_service_action)));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sysSettings.add(new SysSettings(context.getString(R.string.autofill_service_info), context.getString(R.string.autofill_service_desc), Boolean.valueOf(isAutofillServiceEnabled()).toString(), Boolean.FALSE.toString(), "autofillSettings", context.getString(R.string.autofill_service_action)));
        }
        sysSettings.add(new SysSettings(context.getString(R.string.bluetooth_on_info), context.getString(R.string.bluetooth_on_desc), Boolean.valueOf(isBluetoothOn()).toString(), Boolean.FALSE.toString(), "disableBluetooth", context.getString(R.string.bluetooth_on_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.wifi_on_info), context.getString(R.string.wifi_on_desc), Boolean.valueOf(isWifiEnabled()).toString(), Boolean.FALSE.toString(), "disableWifi", context.getString(R.string.wifi_on_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.debug_app_info), context.getString(R.string.debug_app_desc), Boolean.valueOf(debugableAppsDisabled()).toString(), Boolean.TRUE.toString(), "openDevSettings", context.getString(R.string.debug_app_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.adb_enabled_info), context.getString(R.string.adb_enabled_desc), Boolean.valueOf(isADBEnabled()).toString(), Boolean.FALSE.toString(), "openDevSettings", context.getString(R.string.adb_enabled_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.wifi_scan_always_enabled_info), context.getString(R.string.wifi_scan_always_enabled_desc), Boolean.valueOf(isWifiScanEnabled()).toString(), Boolean.FALSE.toString(), "disableAlwaysWifiScan", context.getString(R.string.wifi_scan_always_enabled_action)));

        sysSettings.add(new SysSettings(context.getString(R.string.ble_scan_always_enabled_info), context.getString(R.string.ble_scan_always_enabled_desc), Boolean.valueOf(isBleScanAlwaysOn()).toString(), Boolean.FALSE.toString(), "disableAlwaysBleScan", context.getString(R.string.ble_scan_always_enabled_action)));

        /*sysSettings.add(new SysSettings("Assisted GPS (A-GPS) works on the same principles as a GPS, " +
                "the difference is that it gets the information from the satellites by using network " +
                "resources e.g. mobile network.",
                "Assisted GPS",
                Boolean.valueOf(isAssistedGPSEnabled()).toString(),
                Boolean.FALSE.toString(),
                "disableLocation", "go to location settings and disable the following if not needed" +
                " : \n Emergency Location Service, google Location accuracy, Google Location History"));*/

        // Insert SysSettings into database
        Utilities.INSTANCE.insertSettingsScope(context, sysSettings);

        return sysSettings;
    }


    public boolean isScreenLockEnabled() {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return km.isDeviceSecure();
        }
        return false;
    }

    public void screenLockSettings() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
        context.startActivity(intent);
    }


    public boolean isPatternVisible() {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD) != 0;
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return true;
    }

    public boolean makePatternInvisible() {
        try {
            android.provider.Settings.System.putString(context.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD, "0");
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }


    public boolean screenLockTimeoutEnabled() {

        return dpm.getMaximumTimeToLock(compName) <= 60000;
    }

    public boolean setScreenLockTimeoutEnabled() {
        // TODO: Error when no admin permission granted
        try {
            if (dpm.isAdminActive(compName)) {
                dpm.setMaximumTimeToLock(compName, 60000);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;

    }


    public boolean isDevSettingsEnabled() {
        try {
            return android.provider.Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public void disableDevSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        context.startActivity(intent);
    }

    public boolean isInstallUnknownAppEnabled() {
        try {
            return android.provider.Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean disableInstallUnknownApp() {
        try {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }


    /*public boolean isSimCardLocked() {

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int state = manager.getSimState();
        if (state == TelephonyManager.SIM_STATE_PIN_REQUIRED ) {
            return true;
        }
        return false;
    }


    public void setSimCardLock() {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        context.startActivity(intent);
    }*/


    public boolean isNetTimeEnabled() {
        try {
            if (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME) != 1) {
                return false;
            }

            if (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE) != 1) {
                return false;
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return true;
    }

    public boolean setNetTimeAuto() {
        try {
            Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;

    }


    public boolean isImproveAppDetectionEnabled() {
        try {
            return Settings.Global.getInt(context.getContentResolver(), "upload_apk_enable") != 0;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean checkScreenTimeOut() {
        try {
            return android.provider.Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT) <= 60000;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean setScreenTimeOut() {
        try {
            dpm.setMaximumTimeToLock(compName, 60000L);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;

    }


    public boolean isAddUsersLockScreenEnabled() {
        try {
            return android.provider.Settings.Global.getInt(context.getContentResolver(),
                    "add_users_when_locked") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean fixAddUsersLockScreenEnabled() {
        try {
            // TODO: crash on Samsung device
            Intent intent = new Intent("com.android.settings");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intent.setClassName("com.android.settings", "com.android.settings.Settings$UserSettingsActivity");
            } else {
                intent.setClassName("com.android.settings", "com.android.settings.Settings$UserAndAccountDashboardActivity");
            }
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }


    public boolean isLockShowNotificationsEnabled() {
        try {
            if (android.provider.Settings.Secure.getInt(context.getContentResolver(),
                    "lock_screen_show_notifications") != 0) {
                return true;
            }
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public boolean setLockShowNotifications() {
        try {
            Intent intent = new Intent("com.android.settings");
            intent.setClassName("com.android.settings",
                    "com.android.settings.Settings$ConfigureNotificationSettingsActivity");

            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;

    }


    public boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean disableLocation() {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }


    public boolean isVoiceAssistanceEnabled() {
        /**
         * TODO: case of OnePlus that use "oneplus_default_voice_assist_picker_service" instead
         *        of "voice_interaction_service" and "assistant"
         */

        if (android.provider.Settings.Secure.getString(context.getContentResolver(), "assistant") != null && !android.provider.Settings.Secure.getString(context.getContentResolver(), "assistant").isEmpty()) {
            return true;
        }

        return Settings.Secure.getString(context.getContentResolver(), "voice_interaction_service") != null && !Settings.Secure.getString(context.getContentResolver(), "voice_interaction_service").isEmpty();
    }

    public boolean setVoiceAssistance() {

        try {
            Intent intent = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        /*
        try {
            Intent intent = new Intent("com.android.settings");
            intent.setClassName("com.android.settings", "com.android.settings.Settings$ManageAssistActivity");

            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }*/
        return true;
    }


    public boolean isDefaultNotifManager() {
        if (!android.provider.Settings.Secure.getString(context.getContentResolver(), "enabled_notification_policy_access_packages").isEmpty())
            return false;

        return Settings.Secure.getString(context.getContentResolver(), "enabled_notification_assistant").isEmpty();
    }

    public boolean isAutofillServiceEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager afm = context.getSystemService(AutofillManager.class);
            return afm.isEnabled();
        }

        return Settings.Secure.getString(context.getContentResolver(), "autofill_service") != null && !Settings.Secure.getString(context.getContentResolver(), "autofill_service").isEmpty();
    }


    public boolean autofillSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            //Intent intent = new Intent("com.android.settings");
            //intent.setClassName("com.android.settings", "com.android.settings.Settings$LanguageAndInputSettingsActivity");
            context.startActivity(intent);
            return true;
            /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent("com.android.settings");
                intent.setClassName("com.android.settings", "com.android.settings.Settings$LanguageAndInputSettingsActivity");
                context.startActivity(intent);
            }
            Intent intent = new Intent("com.google.settings");
            intent.setClassName("com.android.settings", "com.android.settings.Settings$LanguageAndInputSettingsActivity");
            //intent.setClassName("com.android.settings", "com.android.settings.Settings$InputMethodAndLanguageSettingsActivity");

            context.startActivity(intent);
            return true;*/
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        try {
            context.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            return true;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }


        return false;
    }

    public boolean isBluetoothOn() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), Settings.Global.BLUETOOTH_ON) != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean disableBluetooth() {
        //try {

        try {
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            context.startActivity(intent);
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage());
            return false;
        }
        return true;
    }


    public boolean isDeviceProvisioned() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "device_provisioned") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public boolean isUSBMassStorageEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "usb_mass_storage_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    // TODO
    public void usbConnectionSettings() {
    }

    public boolean isWifiEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "wifi_on") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean disableWifi() {
        try {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
        return true;
    }

    public boolean debugableAppsDisabled() {
        String debugApp = android.provider.Settings.Global.getString(context.getContentResolver(), "debug_app");
        return (debugApp == null || (debugApp.isEmpty() || debugApp == "null"));
    }

    public void openDevSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        context.startActivity(intent);
    }

    public boolean isADBEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "adb_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isWifiScanEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "wifi_scan_always_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public void disableAlwaysWifiScan() {

        try{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


    public boolean isTouchExplorationEnabled() {
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), "touch_exploration_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public boolean isBleScanAlwaysOn() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "ble_scan_always_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public void disableAlwaysBleScan() {
        try{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //intent.setClassName("com.android.settings", "com.android.settings.Settings$ScanningSettingsActivity");

            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        /*Intent intent = new Intent("com.android.settings");

        intent.setClassName("com.android.settings", "com.android.settings.Settings$LocationSettingsActivity");

        context.startActivity(intent);*/
    }

    public boolean isDefaultInstallLocationChanged() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "default_install_location") == 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public void setDefaultInstallLocation() {
        Settings.Global.putInt(context.getContentResolver(), "default_install_location", 0);
    }

    public boolean isPackageVerifierEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "package_verifier_enable") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isSpeakPasswordEnabled() {
        try {
            if (android.provider.Settings.Secure.getInt(context.getContentResolver(), "speak_password") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isWakeGestureEnabled() {
        try {
            if (android.provider.Settings.Secure.getInt(context.getContentResolver(), "wake_gesture_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isAssistStructure() {
        try {
            if (android.provider.Settings.Secure.getInt(context.getContentResolver(), "assist_structure_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isAssistedGPSEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "assisted_gps_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isNetworkRecommendationsEnabled() {
        try {
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), "network_recommendations_enabled") != 0)
                return true;
        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isUnknownSourceReserver() {
        try {
            return android.provider.Settings.Secure.getInt(context.getContentResolver(), "unknown_sources_default_reversed") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isLockScreenDisabled() {
        try {
            return android.provider.Settings.Secure.getInt(context.getContentResolver(), "lockscreen.disabled") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isLockScreenOwnerInfoEnabled() {
        try {
            return android.provider.Settings.Secure.getInt(context.getContentResolver(), "lock_screen_owner_info_enabled") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isBluetoothAddrValid() {
        try {
            return android.provider.Settings.Secure.getInt(context.getContentResolver(), "bluetooth_addr_valid") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }


    public boolean isStayOnWhilePluggedIn() {
        try {
            return android.provider.Settings.Global.getInt(context.getContentResolver(), "stay_on_while_plugged_in") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isWaitingForDebugger() {
        try {
            return android.provider.Settings.Global.getInt(context.getContentResolver(), "wait_for_debugger") != 0;

        } catch (android.provider.Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    // TODO: Encryption settings global require_password_to_decrypt


    // TODO: check allowed_geolocation_origins in secure


    // TODO: check enabled_input_method value => no voice
}


