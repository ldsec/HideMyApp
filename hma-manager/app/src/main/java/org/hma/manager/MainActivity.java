package org.hma.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

//TODO: Add option to uninstall installed apps

public class MainActivity extends Activity {

    private Button mInstallNewAppButton;
    private Button mListInstalledAppsButton;
    private ImageButton mInstallNewAppImageButton;
    private static final int INITIAL_REQUEST=1337;

    private static final String[] STORAGE_PERMS={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasExternalStoragePermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(STORAGE_PERMS, INITIAL_REQUEST);
            }
        }

        mInstallNewAppButton = (Button) findViewById(R.id.installNewAppButton);
        mListInstalledAppsButton = (Button) findViewById(R.id.listInstalledAppsButton);
        //mInstallNewAppImageButton = (ImageButton) findViewById(R.id.installNewAppimageButton);

        mInstallNewAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), InstallApp.class));

            }
        });

        mListInstalledAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LaunchApp.class));
            }
        });

//        mInstallNewAppImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), InstallApp.class));
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //PERMISSION GRANTED
                } else {
                    //PERMISSION NOT GRANTED
                    mInstallNewAppButton.setEnabled(false);
                    Toast.makeText(this, "Access to external storage required to install apps!",
                            Toast.LENGTH_LONG).show();
                }
                return;
        }
    }


    boolean hasExternalStoragePermission(){
        return hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                & hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
        }
        else{
            return true;  //Older versions of Android
        }
    }

}

