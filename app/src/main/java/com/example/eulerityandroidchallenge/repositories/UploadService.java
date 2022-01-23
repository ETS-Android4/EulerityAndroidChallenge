package com.example.eulerityandroidchallenge.repositories;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 *      A retrofit service for uploading data
 */

public interface UploadService {

    @Multipart
    @POST("{fullUrl}")
    Call<ResponseBody> upload (@Path(value = "fullUrl", encoded = true) String fullUrl, @Part MultipartBody.Part appid, @Part MultipartBody.Part original, @Part MultipartBody.Part file);
}
