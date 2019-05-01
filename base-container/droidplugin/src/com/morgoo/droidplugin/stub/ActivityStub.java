package com.morgoo.droidplugin.stub;

import android.app.Activity;
import android.content.res.Configuration;

public abstract class ActivityStub extends Activity {

    private static class SingleInstanceStub extends ActivityStub {}

    private static class SingleTaskStub extends ActivityStub {}

    private static class SingleTopStub extends ActivityStub {}

    private static class StandardStub extends ActivityStub {}

    public static class P08{

        public static class SingleInstance00 extends SingleInstanceStub {}

        public static class SingleTask00 extends SingleTaskStub {}

        public static class SingleTop00 extends SingleTopStub {}

        public static class Standard00 extends StandardStub {}

    }


    public static class Dialog {

        public static class P08 {

            public static class SingleInstance00 extends SingleInstanceStub {}

            public static class SingleTask00 extends SingleTaskStub {}

            public static class SingleTop00 extends SingleTopStub {}

            public static class Standard00 extends StandardStub {}
        }
    }
}
