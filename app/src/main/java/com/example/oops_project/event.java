package com.example.oops_project;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class event {
    private String id;
    private long eventId;
    private String name;
    private String description;
    private String date, time;

    public event(String id, String name, String description, String date, String time, long eventId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getCalendar() {
        Calendar obj = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:aa", Locale.ENGLISH);
        try {
            obj.setTime(sdf.parse(getDate() + " " + getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }

    @Override
    public String toString()
    {
        String s = "[" +
                "\n" + id +
                "\n" + eventId +
                "\n" + name +
                "\n" + description +
                "\n" + date +
                "\n" + time +
                "\n]\n";
        return s;
    }



}