package com.example.eulerityandroidchallenge.photoediting.filters;

import ja.burhanrashid52.photoeditor.PhotoFilter;

/**
 *      Listener interface which executes when a new filter is selected
 */

public interface FilterListener {
    void onFilterSelected(PhotoFilter photoFilter);
}