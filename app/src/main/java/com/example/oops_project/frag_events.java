package com.example.oops_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class frag_events extends Fragment {

    FloatingActionButton add_event;
    private RecyclerView eventRecView;
    private eventRecViewAdapter adapter;
    private ArrayList<event> events;

    public frag_events(ArrayList<event> events, FloatingActionButton add) {
        this.events = events;
        this.add_event = add;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        adapter = new eventRecViewAdapter(getActivity());
        eventRecView = view.findViewById(R.id.eventsRecView);
        eventRecView.setAdapter(adapter);
        eventRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView inst =view.findViewById(R.id.event_instruction);
        if(events.size() == 0)inst.setVisibility(View.VISIBLE);else inst.setVisibility(View.GONE);

        adapter.setEvents(events);

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Adding Event!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
