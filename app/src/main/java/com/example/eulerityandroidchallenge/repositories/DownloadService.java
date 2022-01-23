package com.example.eulerityandroidchallenge.repositories;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 *      A retrofit service for downloading data
 */

public interface DownloadService {

    @GET("/image")
    Call<ResponseBody> download ();
}
