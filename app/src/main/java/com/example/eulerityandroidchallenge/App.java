package com.example.eulerityandroidchallenge;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 *      A singleton subclass of Application that allows access to resources from outside a context
 */

public class App extends Application {

    private static App instance;

    public static Resources getResourcesStatic() {
        return instance.getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static void logMethod (String tag, String message) {
        Log.d(tag, message);
    }
}
