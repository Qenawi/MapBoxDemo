package com.panda.stuedent_map_1;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
