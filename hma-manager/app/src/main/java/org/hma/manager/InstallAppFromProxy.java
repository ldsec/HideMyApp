package org.hma.manager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class InstallAppFromProxy extends Activity {

    Button mDownloadApkButton;
    private DownloadManager mgr=null;
    private long downloadId=-1L;
    private String remoteApkUrl = "http://www.hma.org/apks/hma-app1.apk";
    private String remoteApkName = "hma-app1.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_app_from_proxy);


        mDownloadApkButton = (Button)findViewById(R.id.downloadApkButton);
        mDownloadApkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InstallAppFromProxy.this, "Downloading APK",
                        Toast.LENGTH_SHORT).show();

                //TODO: Prompt user for permissions required.

                // Using DownloadManager to get the container APK from the proxy
                // Based on example from the BCGAD book:
                // https://github.com/commonsguy/cw-omnibus/blob/master/Internet/Download/app/src/main/java/com/commonsware/android/downmgr/DownloadFragment.java

                final Uri remoteApkUri= Uri.parse(remoteApkUrl);
                DownloadManager.Request req = new DownloadManager.Request(remoteApkUri);
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("HMA-App")
                        .setDescription("HMA-App downloading...")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                remoteApkName);

                //Create directory if it does not already exists
                //TODO: Is this needed?
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .mkdirs();

                mgr = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                downloadId=mgr.enqueue(req);


                // Install APK after it is downloaded
                // Based on example from SO:
                // https://stackoverflow.com/questions/4967669/android-install-apk-programmatically

                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {

                        //Note: Do not use Environment.DIRECTORY_DOWNLOADS for downloadedFile, otherwise
                        //FileProvider will fail
                        File downloadedFile =
                                new File(Environment.getExternalStorageDirectory()+
                                        "/Download/"+
                                        remoteApkName);


                        // Different procedures to install apk from Android 7.0
                        // https://stackoverflow.com/questions/39147608/android-install-apk-with-intent-view-action-not-working-with-file-provider/40131196#40131196

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri apkUri = FileProvider.getUriForFile(InstallAppFromProxy.this,
                                    BuildConfig.APPLICATION_ID, downloadedFile);
                            Intent intentInstall = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            intentInstall.setData(apkUri);
                            intentInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intentInstall);
                        } else {
                            Uri apkUri = Uri.fromFile(downloadedFile);
                            Intent intentInstall = new Intent(Intent.ACTION_VIEW);
                            intentInstall.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentInstall);
                        }

                        unregisterReceiver(this);
                        finish();
                    }
                };

                //register receiver for when .apk download is compete
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                //TODO: Delete container APK after installation


            }
        });
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
