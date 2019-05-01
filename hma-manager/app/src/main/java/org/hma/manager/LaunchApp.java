package org.hma.manager;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LaunchApp extends ListActivity {

    private String installedApps[];
    private final String HMASufix = "org.hma.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_app);

        installedApps = getHMAAps();
        setListAdapter(new LaunchApp.IconicAdapter());
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        Intent launchAppIntent = getPackageManager().getLaunchIntentForPackage(installedApps[position]);
        startActivity(launchAppIntent);
    }

    private String[] getHMAAps(){

        ArrayList<String> appList = new ArrayList<String>();

        List<PackageInfo> installedPackagesList = getPackageManager().getInstalledPackages(0);

        for(PackageInfo pkgInfo: installedPackagesList){

            if(pkgInfo.packageName.startsWith(HMASufix)){
                appList.add(pkgInfo.packageName);
            }
        }

        return appList.toArray(new String[appList.size()]);
    }
    class IconicAdapter extends ArrayAdapter<String> {

        IconicAdapter() {
            super(LaunchApp.this, R.layout.row, R.id.label, installedApps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TargetAppInfo targetAppInfo = new TargetAppInfo(installedApps[position]);
            View row=super.getView(position, convertView, parent);
            ImageView icon=(ImageView)row.findViewById(R.id.icon);
            icon.setImageResource(targetAppInfo.getTargetAppIconName());
            TextView size=(TextView)row.findViewById(R.id.targetAppName);
            size.setText(targetAppInfo.getTargetAppName());
            return(row);
        }
    }
 }
