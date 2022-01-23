package com.example.eulerityandroidchallenge.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.eulerityandroidchallenge.App;
import com.example.eulerityandroidchallenge.R;
import com.example.eulerityandroidchallenge.models.ImageObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *      A singleton repository class that fetches the images and their metadata from /image
 *      and passes them to the ViewModel as an List of ImageObjects
 */

public class ImageObjectRepository {

    private static ImageObjectRepository instance;
    private final ArrayList<ImageObject> dataSet = new ArrayList<>();
    MutableLiveData<List<ImageObject>> data;

    public static ImageObjectRepository getInstance() {
        if (instance == null) {
            instance = new ImageObjectRepository();
        }
        return instance;
    }

    //fetch data from /image
    public MutableLiveData<List<ImageObject>> getImageObjects () {
        downloadImageObjects();data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    //downloads JSONArray from /image and passes it to setImageObjects()
    private void downloadImageObjects () {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(App.getResourcesStatic().getString(R.string.base_url)).build();
        DownloadService service = retrofit.create(DownloadService.class);

        Call<ResponseBody> call = service.download();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    setImageObjects(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //converts the JSONArray from /image into an ArrayList<ImageObjects> and posts it to the MutableLiveData object
    private void setImageObjects (String str) {
        try {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String url = obj.getString("url");
                String created = obj.getString("created");
                String updated = obj.getString("updated");
                dataSet.add(new ImageObject(url, created, updated));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data.postValue(dataSet);
    }
}
