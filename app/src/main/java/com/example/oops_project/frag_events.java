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

public class frag_events extends Fragment {

    private RecyclerView eventRecView;
    private eventRecViewAdapter adapter;
    private ArrayList<event> events;

    public frag_events(ArrayList<event> events) {
        this.events = events;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events , container , false);
        adapter = new eventRecViewAdapter(getActivity());
        eventRecView = view.findViewById(R.id.eventsRecView);
        eventRecView.setAdapter(adapter);
        eventRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setEvents(events);

        return view;
    }
}
