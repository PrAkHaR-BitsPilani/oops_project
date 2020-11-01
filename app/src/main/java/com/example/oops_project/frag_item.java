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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items , container , false);
        itemRecView = view.findViewById(R.id.itemRecView);
        adapter = new itemRecViewAdapter(getActivity());
        itemRecView.setAdapter(adapter);
        itemRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // sample data

        ArrayList<item> items = new ArrayList<>();
        items.add(new item("0" , "Milk", 24, 5, "https://i0.wp.com/post.healthline.com/wp-content/uploads/2019/11/milk-soy-hemp-almond-non-dairy-1296x728-header-1296x728.jpg?w=1155&h=1528.jpg"));
        items.add(new item("1" , "Potato", 30, 5, "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTObd35g74rosg3jPC8qxp4vLF_q3f1AFYWvQ&usqp=CAU.jpg"));
        items.add(new item("2", "Ketchup", 120,3 ,"https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQgg8YHhZAhDxvA0lpwqpGkIhN_uxVYr57noA&usqp=CAU.jpg"));
        adapter.setItems(items);
        return view;
    }

}
