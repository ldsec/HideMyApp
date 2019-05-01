package org.hma.manager;

/**
 * Created by idacosta on 05/11/17.
 */

public class TargetAppInfo {

    private String containerPkgName;
    private String targetAppName;
    private int targetAppIconName;

    public TargetAppInfo(String containerPkgName){

 if (containerPkgName.equals("org.hma.app1")){
            this.containerPkgName = containerPkgName;
            this.targetAppName = "Cancer.Net";
            this.targetAppIconName = R.drawable.cancernet_icon;
        }
        else if (containerPkgName.equals("org.hma.app2")){
            this.containerPkgName = containerPkgName;
            this.targetAppName = "What's Up?";
            this.targetAppIconName = R.drawable.whatsup_icon;
        }
        else if (containerPkgName.equals("org.hma.app3")){
            this.containerPkgName = containerPkgName;
            this.targetAppName = "CatchMyPain";
            this.targetAppIconName = R.drawable.catchmypain_icon;
        }
        else if (containerPkgName.equals("org.hma.app4")) {
            this.containerPkgName = containerPkgName;
            this.targetAppName = "QuitNow! Quit smoking";
            this.targetAppIconName = R.drawable.quitnow_icon;
        }
         else if (containerPkgName.equals("org.hma.app5")) {
            this.containerPkgName = containerPkgName;
            this.targetAppName = "AsthmaMD";
            this.targetAppIconName = R.drawable.asthmamd_icon;
        }
        else {
            this.containerPkgName = containerPkgName;
            this.targetAppName = "Unknown";
            this.targetAppIconName = R.drawable.unknown;
        }
    }


    public String getContainerPkgName() {
        return containerPkgName;
    }

    public String getTargetAppName() {
        return targetAppName;
    }

    public int getTargetAppIconName() {
        return targetAppIconName;
    }
}
