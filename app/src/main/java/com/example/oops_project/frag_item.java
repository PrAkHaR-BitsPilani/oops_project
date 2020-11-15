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

public class frag_item extends Fragment {

    private RecyclerView itemRecView;
    private itemRecViewAdapter adapter;
    ArrayList<item>items = new ArrayList<>();

    public frag_item(ArrayList<item> items) {
        this.items = items;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items , container , false);
        itemRecView = view.findViewById(R.id.itemRecView);
        adapter = new itemRecViewAdapter(getActivity());
        itemRecView.setAdapter(adapter);
        itemRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setItems(items);
        return view;
    }

}
