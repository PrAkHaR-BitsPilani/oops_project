package com.example.oops_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class frag_inventory extends Fragment {

    private RecyclerView categoryRecView;
    private categoryRecViewAdapter adapter;
    ArrayList<category> categories;

    public frag_inventory(ArrayList<category> categories) {
        this.categories = categories;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_inventory , container , false);
        adapter = new categoryRecViewAdapter(getActivity(), getFragmentManager());
        categoryRecView = view.findViewById(R.id.categoryRecView);
        categoryRecView.setAdapter(adapter);
        categoryRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setCategories(categories);

        return view;
    }

}
