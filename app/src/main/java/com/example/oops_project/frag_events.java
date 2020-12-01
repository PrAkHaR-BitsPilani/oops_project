package com.example.oops_project;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import java.util.Calendar;

public class frag_events extends Fragment {

    FloatingActionButton add_event;
    private RecyclerView eventRecView;
    private eventRecViewAdapter adapter;
    private final ArrayList<event> events;
    private ContentResolver contentResolver;
    private Cursor cursor;

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
                addEventDialog dialog = new addEventDialog();

                dialog.setTransferCall(new addEventDialog.transferCall() {
                    @Override
                    public void onSaveEvent(String name, String des, String date, String time) {
                        event e = new event(events.size()+"", name, des, date, time, 0);
                        events.add(e);
                        boolean flag = addEventToCalendar(e, 1);

                        adapter.notifyItemInserted(events.size());
                        inst.setVisibility(View.GONE);
                        if(flag)
                            Toast.makeText(getActivity(), name + " added!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Unknown error occurred!" + e.getEventId(), Toast.LENGTH_LONG).show();
                    }
                });

                dialog.show(getFragmentManager() , "Adding event");
            }
        });

        return view;
    }

    public boolean addEventToCalendar(event e, int reminder) {
        Calendar cal = e.getCalendar();

        contentResolver = getActivity().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, e.getName());
        contentValues.put(CalendarContract.Events.DESCRIPTION, e.getDescription());
        contentValues.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
        contentValues.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
        if(uri == null) {
            Toast.makeText(getActivity(), "Here", Toast.LENGTH_SHORT).show();
            return false;
        }

        long eventID = Long.parseLong(uri.getLastPathSegment());
        e.setEventId(eventID);


        contentValues = new ContentValues();
        contentValues.put(CalendarContract.Reminders.MINUTES, reminder);
        contentValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);

        contentValues.put(CalendarContract.Reminders.MINUTES, 10);
        uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);

        contentValues.put(CalendarContract.Reminders.MINUTES, 30);
        uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);

        contentValues.put(CalendarContract.Reminders.MINUTES, 60);
        uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);


        return (uri != null);
    }




}
