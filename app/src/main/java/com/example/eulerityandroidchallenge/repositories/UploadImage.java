package com.example.eulerityandroidchallenge.repositories;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.example.eulerityandroidchallenge.App;
import com.example.eulerityandroidchallenge.R;
import com.example.eulerityandroidchallenge.models.ImageObject;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *      A singleton class that interacts with the website to fetch the upload URL and then uploads the image
 */

public class UploadImage {

    private static UploadImage instance;

    MutableLiveData<Boolean> data = new MutableLiveData<>();

    public static UploadImage getInstance () {
        if (instance == null) {
            instance = new UploadImage();
        }
        return instance;
    }

    //Implements UploadService to upload the image
    private void upload (ImageObject obj, Bitmap bitmap) {
        String url = getUploadURL();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).build();
        UploadService service = retrofit.create(UploadService.class);

        MultipartBody.Part appid = MultipartBody.Part.createFormData("appid", App.getResourcesStatic().getString(R.string.app_id));
        MultipartBody.Part original = MultipartBody.Part.createFormData("original", obj.getUrl());

        String[] originalUrl = obj.getUrl().split("/");
        String fileName = originalUrl[originalUrl.length-1];
        File f = App.bitmapToFile(bitmap, "tmp.jpeg");
        RequestBody imageFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        MultipartBody.Part file = MultipartBody.Part.createFormData("file", fileName, imageFile);

        Call<ResponseBody> call = service.upload(url, appid, original, file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    data.postValue(true);
                    f.delete();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    //Reports a MutableLiveData<Boolean> to the Activity to notify the user of a successful upload
    public MutableLiveData<Boolean> getSuccess (ImageObject obj, Bitmap bitmap) {
        upload(obj, bitmap);
        data.setValue(false);
        return data;
    }

    //Uses a Callable object to fetch the upload URL asynchronously and returns it to upload()
    public String getUploadURL () {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future = executor.submit(new GetUploadURLCallable());

        String url = "";
        try {
            url = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return url;
    }
}
