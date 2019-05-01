package org.hma.hma_app1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.TimingLogger;

import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.morgoo.droidplugin.pm.PluginManager.getInstance;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

public class MainActivity extends Activity {

    private String targetApkFilePath;
    private String targetApkFileName = "target.apk";
    private Boolean targetApkFileReady = true;
    private String tag;
    public static final String PREFS_NAME = "hmaPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        this.tag = this.getPackageName();
        targetApkFilePath = this.getFilesDir().toString() + "/" + targetApkFileName;

        // If the target APK file does not exist in the container private directory, copy it first
        if (!checkTargetApkFileExists() && isPluginDirEmpty()){
            long elapsedTime;
            elapsedTime = android.os.SystemClock.uptimeMillis();
            targetApkFileReady = copyTargetApk();
            PackageInfo info = getPackageManager().getPackageArchiveInfo(targetApkFilePath, PackageManager.GET_ACTIVITIES);
            savePersistantStr("pkgName", info.packageName);
            elapsedTime = android.os.SystemClock.uptimeMillis() - elapsedTime;
            Log.d(tag, "HMA APK copy time: " + elapsedTime + " ms");
        }

        if(targetApkFileReady) {
            String pkgName = readPersistantStr("pkgName");
            if (isAppInstalled(pkgName)) {
                Log.d(tag, pkgName + " already installed!");
                runInstalledApp(pkgName);
            } else {
                Log.d(tag, pkgName + " has not been installed!");
                installApp(pkgName);
            }
        }else{
            Log.e(tag, "Target APK file not ready");
        }
    }

    /***
     * Runs the app (assumes the app has been already installed.
     * @param pkgName
     */

    private void runInstalledApp(String pkgName){
        Intent intent = getPackageManager().getLaunchIntentForPackage(pkgName);

        // Retry getting intent, as it may take some time after a force-stop operation
        while(intent == null){
            try {
                Thread.sleep(50);
                intent = getPackageManager().getLaunchIntentForPackage(pkgName);
                Log.d(tag, "HMA TEST-SLEEP");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(tag, "start " + pkgName + "@" + intent);
            startActivity(intent);
            finish(); //Kill this activity, so user cannot see it by mistake (pressing back button)
        }else {
            Log.e(tag, "Error launching " + pkgName + "@" + intent);
        }
    }

    /***
     * Install the application in DroidPlugin (Host app)
     * @param pkgName
     */
    private void installApp(String pkgName){

        try {
            final int re =  getInstance().installPackage(targetApkFilePath, 0);
            switch (re) {
                case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                    Log.e(tag, "The installation failed, too many permissions on the file request");
                    break;
                case INSTALL_FAILED_NOT_SUPPORT_ABI:
                    Log.e(tag, "The host does not support the plugin's abi environment, which may host 64-bit runtime, but the plugin only supports 32-bit");
                    break;
                case INSTALL_SUCCEEDED:
                    Log.i(tag, "The installation is complete");
                    if(!deleteTargetApk()) {  //Delete this file, as DroidPlugin makes a copy during installation
                        Log.e(tag, "Error deleting the target APK file");
                    }
                    runInstalledApp(pkgName);
                    break;
                default:
                    Log.e(tag, "The installation failed, not clear why (default switch case)");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check if the app is already installed in DroidPlugin
     * @param pkgName
     * @return True if the app is already installed
     */
    public Boolean isAppInstalled(String pkgName){
        try {
            List<ApplicationInfo> installedApps = PluginManager.getInstance().getInstalledApplications(0);

            for (ApplicationInfo appInfo: installedApps){

                if (appInfo.packageName.equals(pkgName)){
                    return true;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean copyTargetApk(){
        try {
            File file = new File(this.getFilesDir().toURI().resolve(targetApkFileName));
            InputStream is = this.getAssets().open(targetApkFileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte buf[]=new byte[1024];
            int len;
            while((len=is.read(buf))>0)
                fos.write(buf,0,len);
            fos.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(tag, "Problem copying target apk file to container private directory: " + e.getMessage());
            return false;
        }
        Log.d(tag, "target APK copied");
        return true;
    }

    private Boolean deleteTargetApk(){
        return getApplicationContext().deleteFile(targetApkFileName);
    }

    private Boolean checkTargetApkFileExists(){
        File file = new File(this.getFilesDir().toURI().resolve(targetApkFileName));

        Log.d(tag, this.getFilesDir().toURI().resolve(targetApkFileName).toString());

        if (file.exists()){
            return true;
        }
        return false;
    }

    /**
     * Check if the DroidPlugin directory containing the APK info is empty to avoid
     * copying the target.apk file twice (DroidPlugin deletes it after first installation)
     *
     * @return True if Plugin
     */

    private Boolean isPluginDirEmpty(){
        File dir = new File(this.getFilesDir().toURI().resolve("../Plugin"));
        if (dir.list().length != 0){
            return false;
        }
        return true;
    }
    Boolean savePersistantStr(String strName, String strVal){
        SharedPreferences persistanStrs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = persistanStrs.edit();
        editor.putString(strName, strVal);
        return editor.commit();
    }

    String readPersistantStr(String strName){
        SharedPreferences persistanStrs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return persistanStrs.getString(strName,"");
    }
}
