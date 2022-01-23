package com.example.eulerityandroidchallenge.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eulerityandroidchallenge.models.ImageObject;
import com.example.eulerityandroidchallenge.repositories.UploadImage;

/**
 *      A ViewModel which does the following:
 *          1. Sends an ImageObject and a Bitmap to be uploaded by UploadImage
 *          2. Receives upload status updates from ImageObject and exposes them to EditImageActivity
 */

public class UploadStatus extends ViewModel {

    private MutableLiveData<Boolean> success;
    UploadImage uploadImage;

    public void init (ImageObject obj, Bitmap bitmap) {
        uploadImage = UploadImage.getInstance();
        success = uploadImage.getSuccess(obj, bitmap);
    }

    public LiveData<Boolean> getUploadStatus () {
        return success;
    }

}
