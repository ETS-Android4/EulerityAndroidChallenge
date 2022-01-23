package com.example.eulerityandroidchallenge.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 *      A data model that represents the images and metadata fetched from /image
 */

public class ImageObject implements Parcelable {

    private final String url;
    private final String created;
    private final String updated;

    public ImageObject (String url, String created, String updated) {
        this.url = url;
        this.created = created;
        this.updated = updated;
    }

    protected ImageObject(Parcel in) {
        url = in.readString();
        created = in.readString();
        updated = in.readString();
    }

    public static final Creator<ImageObject> CREATOR = new Creator<ImageObject>() {
        @Override
        public ImageObject createFromParcel(Parcel in) {
            return new ImageObject(in);
        }

        @Override
        public ImageObject[] newArray(int size) {
            return new ImageObject[size];
        }
    };

    public String getUrl() {
        return url;
    }

    @NonNull
    public String toString () {
        return this.url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(created);
        parcel.writeString(updated);
    }
}
