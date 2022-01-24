package com.example.eulerityandroidchallenge;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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

    //Converts bitmaps to JPEG files
    public static File bitmapToFile (Bitmap bitmap, String fileNameToSave) {
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "" + fileNameToSave);
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; //returns null
        }
    }
}
