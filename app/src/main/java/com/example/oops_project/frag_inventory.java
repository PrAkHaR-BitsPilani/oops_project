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

import java.util.ArrayList;

public class frag_inventory extends Fragment {

    private RecyclerView categoryRecView;
    private categoryRecViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_inventory , container , false);
        adapter = new categoryRecViewAdapter(getActivity(), getFragmentManager());
        categoryRecView = view.findViewById(R.id.categoryRecView);
        categoryRecView.setAdapter(adapter);
        categoryRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // sample data
        ArrayList<category> categories = new ArrayList<>();
        categories.add(new category(0,"Pantry" , "Contains vegetables and other milk products" , "https://static01.nyt.com/images/2020/03/14/dining/23pantry1/23pantry1-superJumbo.jpg"));
        categories.add(new category(2,"Stationary" , "Books, pens and other documents" ,  "https://cdn.thewirecutter.com/wp-content/uploads/2018/07/pens-2x1-0025.jpg"));
        categories.add(new category(2,"Pet Products" , "Care items for pets" ,  "https://i.imgur.com/tGbaZCY.jpg"));
        adapter.setCategories(categories);

        return view;
    }

}
