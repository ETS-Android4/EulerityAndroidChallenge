package com.example.eulerityandroidchallenge.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eulerityandroidchallenge.App;
import com.example.eulerityandroidchallenge.models.ImageObject;
import com.example.eulerityandroidchallenge.repositories.ImageObjectRepository;

import java.util.List;

/**
 *      A ViewModel class that fetches the List of ImageObjects from the ImageObjectRepository and exposes it to MainActivity
 */

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<ImageObject>> imageObjects;

    public void init () {
        if (imageObjects != null) {
            return;
        }
        ImageObjectRepository repo = ImageObjectRepository.getInstance();
        imageObjects = repo.getImageObjects();
    }

    public LiveData<List<ImageObject>> getImageObjects () {
        return imageObjects;
    }

}
