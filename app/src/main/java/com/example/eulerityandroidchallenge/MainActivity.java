package com.example.eulerityandroidchallenge;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eulerityandroidchallenge.adapters.MainAdapter;
import com.example.eulerityandroidchallenge.databinding.ActivityMainBinding;
import com.example.eulerityandroidchallenge.viewmodels.MainActivityViewModel;

/**
 *      The starting screen of the app
 *          - Displays images retrieved from MainActivityViewModel in a RecyclerView
 */

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MainAdapter adapter;

    private MainActivityViewModel viewModel;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.init();
        viewModel.getImageObjects().observe(this, imageObjects -> {
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() > 0) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        initRecyclerView();
    }

    //sets up image recycler view
    private void initRecyclerView () {
        adapter = new MainAdapter(this, viewModel.getImageObjects().getValue());
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
    }
}