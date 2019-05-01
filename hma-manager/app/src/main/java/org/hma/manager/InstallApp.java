package org.hma.manager;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by idacosta on 04/11/17.
 */

public class InstallApp extends ListActivity {
    private String availableAppsToInstall[];
    private String availableAppsPath;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_install_app);

        availableAppsPath = getExternalStorageDirectory().toString()+"/HMA-Apps";
        availableAppsToInstall = getAppList();
        if(availableAppsToInstall !=null) {
            setListAdapter(new IconicAdapter());
        }else{
            Toast.makeText(this, "HMA-Apps directory does not exist!", Toast.LENGTH_SHORT).show();
        }
    }


    private String[] getAppList(){
        String[] appList = new File(availableAppsPath).list();
        if(appList == null){
            return null;
        }else{
            Arrays.sort(appList);
            return appList;
        }
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position,
                                long id) {

        String appName = availableAppsToInstall[position];
        File targetAPK = new File(availableAppsPath+"/"+ appName);
        PackageInfo containerPkgInfo = getPackageManager().getPackageArchiveInfo(targetAPK.getPath(), 0);
        if(isAppAlreadyInstalled(containerPkgInfo.packageName)){
            Toast.makeText(this, "App already installed!", Toast.LENGTH_SHORT).show();
        }else {
            String mimeType = getMimeType(targetAPK.getPath());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(targetAPK), mimeType);
            startActivity(intent);
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    class IconicAdapter extends ArrayAdapter<String> {
        IconicAdapter() {
            super(InstallApp.this, R.layout.row, R.id.label, availableAppsToInstall);
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View row = super.getView(position, convertView, parent);
            String appName = availableAppsToInstall[position];
            if(appName.endsWith(".apk")) {  //Only apk files, otherwise return empty row!
                File targetAPK = new File(availableAppsPath + "/" + appName);
                //TODO: Check file is a real APK file
                PackageInfo containerPkgInfo = getPackageManager().getPackageArchiveInfo(targetAPK.getPath(), 0);
                TargetAppInfo targetAppInfo = new TargetAppInfo(containerPkgInfo.packageName);
                ImageView icon = (ImageView) row.findViewById(R.id.icon);
                icon.setImageResource(targetAppInfo.getTargetAppIconName());
                TextView size = (TextView) row.findViewById(R.id.targetAppName);
                size.setText(targetAppInfo.getTargetAppName());
            }
            return row;
        }
    }

    boolean isAppAlreadyInstalled(String pkgName){
        ArrayList<String> appList = new ArrayList<String>();
        List<PackageInfo> installedPackagesList = getPackageManager().getInstalledPackages(0);
        for(PackageInfo pkgInfo: installedPackagesList){
            if(pkgInfo.packageName.equals(pkgName)){
                return true;
            }
        }
        return false;
    }
}
