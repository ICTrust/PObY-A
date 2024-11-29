

# PObY-A
PObY-A, Privacy Owned by You - Android, is a security and privacy application for Android devices to improve security and privacy of their devices. It uses ClamAV database to scan for malware and warn users about apps with dangerous permissions. It also enforces some settings based on CIS recommendations.

Malware scan is offline and based on two databases:
- ClamAV database, which is updated daily.
- PObY-A database, managed by ICTrust.

## Features

| PObY-A Features                                                |   |
|----------------------------------------------------------------|---|
| 🚫 No Ads or Purchases                                         | ✅ |
| 📙 Translations (FR & EN)                                      | ✅ |
| 🪲 Malware scan                                                | ✅ |
| 🛡️ CVD: ClamAV database integration                           | ✅ |
| 🔐 Enforce settings privacy based on some CIS* recommendations | ✅ |
| ⚠️ Warn apps with dangerous* permissions                       | ✅ |
| ⚡ Many more coming...                                          | ✅ |

**CIS:** Center for Internet Security

**dangerous**: "A higher-risk permission that would give a requesting application access to private user data or control over the device that can negatively impact the user" [permission element](https://developer.android.com/guide/topics/manifest/permission-element)

## Supported Android versions
**8.0 (API 26) to 13 (API 34)**


## Threat scan :
### dHow it works 
Default threat scan use hash database from CVD (HDB) and PObY-A database with malware packages names and certificate.

If the option "Deep Scan" is enabled the CVD NDB database will be used in addition to the previous ones.

> NOTE: Deep Scan can be resource and time consuming as it will scan files of the uncompressed APK. NOT RECOMMENDED with "Scan system Apps" option. 

### Results & classification 
**Malware**: Signature was found on malware database

**SuspiciousCertificat**: The certificate that signed the scanned application was used to sign malware 

**SuspiciousPackageName**: The package name of the application was used also by malware.

> Note ⚠: Some applications could be flagged as "suspicious" means that some malware are disguise as the application or it was signed by the default Android Studio certificate.

## Needed permissions
- Write system settings
- "force-lock" admin permission
- External Storage


## Screenshots
<div style="display: block; margin: auto; margin-left: auto; margin-right: auto;" >
        <img src="screenshots/sidemenu.jpg" alt="drawing" width="200"/>
        <img src="screenshots/dashboard.png" alt="drawing" width="200"/>
        <img src="screenshots/MalwareScan.jpg" alt="drawing" width="200"/>
        <img src="screenshots/noThreatFound.jpg" alt="drawing" width="215"/>
        <img src="screenshots/MalwareScan.png" alt="drawing" width="200"/>
        <img src="screenshots/app_preferences.png" alt="drawing" width="200"/>
        <img src="screenshots/ThreatApp.png" alt="drawing" width="200"/>
        <img src="screenshots/settings.png" alt="drawing" width="200"/>
        <img src="screenshots/ActionSettings.jpg" alt="drawing" width="200"/>
        <img src="screenshots/preferences.png" alt="drawing" width="200"/>
</div>


## Disclaimer
ICTrust is not affiliated with ClamAV.


