package com.example.oops_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;

public class eventRecViewAdapter extends RecyclerView.Adapter<eventRecViewAdapter.ViewHolder> {

    private ArrayList<event> events = new ArrayList<>();
    private final Context mContext;
    private ContentResolver contentResolver;

    public eventRecViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameEvent.setText(events.get(position).getName());
        holder.desEvent.setText(events.get(position).getDescription());
        holder.venueEvent.setText(events.get(position).getDate()+"\n"+events.get(position).getTime());
        holder.cardEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TransitionManager.beginDelayedTransition((holder.cardEvent));
                if (holder.venueEvent.getVisibility() == View.GONE) {
                    holder.venueEvent.setVisibility(View.VISIBLE);
                    holder.editTimeEvent.setVisibility(View.VISIBLE);
                    holder.editDateEvent.setVisibility(View.VISIBLE);
                }

                else {
                    holder.venueEvent.setVisibility(View.GONE);
                    holder.editTimeEvent.setVisibility(View.GONE);
                    holder.editDateEvent.setVisibility(View.GONE);
                }
            }
        });

        holder.editDateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = events.get(position).getCalendar();
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR , year);
                        calendar.set(Calendar.MONTH , month);
                        calendar.set(Calendar.DAY_OF_MONTH , dayOfMonth);

                        if(updateEventInCalendar(events.get(position), calendar)) {
                            Toast.makeText(mContext, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                        }


                        events.get(position).setDate(DateFormat.format("dd/MM/yyyy",calendar).toString());
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext , listener , calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.pink));
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.pink));
            }
        });

        holder.editTimeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        Calendar calendar = events.get(position).getCalendar();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        if(updateEventInCalendar(events.get(position), calendar)) {
                            Toast.makeText(mContext, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                        }

                        events.get(position).setTime(DateFormat.format("hh:mm:aa",calendar).toString());
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),false
                );
                timePickerDialog.show();
                timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.pink));
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.pink));
            }
        });
    }

    public void delete(int pos) {

    }

    public void add(int pos) {

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(ArrayList<event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameEvent;
        private final TextView desEvent;
        private final TextView venueEvent;
        private final MaterialCardView cardEvent;
        private final MaterialButton deleteEvent;
        private final MaterialButton editTimeEvent;
        private final MaterialButton editDateEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameEvent = itemView.findViewById(R.id.nameEvent);
            desEvent = itemView.findViewById(R.id.desEvent);
            venueEvent = itemView.findViewById(R.id.venueEvent);
            cardEvent = itemView.findViewById(R.id.eventParent);
            deleteEvent = itemView.findViewById(R.id.deleteEvent);
            editTimeEvent = itemView.findViewById(R.id.editTimeEvent);
            editDateEvent = itemView.findViewById(R.id.editDateEvent);

            deleteEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext, R.style.MyDialogTheme);
                    dialog.setTitle("Delete " + events.get(getAdapterPosition()).getName());
                    dialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to delete this event?</font>"));
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String where = "_id =" + events.get(getAdapterPosition()).getEventId() + " and " + CalendarContract.Events.CALENDAR_ID + "=" + 1;

                            contentResolver = mContext.getContentResolver();
                            int uri = contentResolver.delete(CalendarContract.Events.CONTENT_URI, where, null);

                            if(uri != 0) {
                                Toast.makeText(mContext, events.get(getAdapterPosition()).getName() + " deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Unknown error occurred!", Toast.LENGTH_SHORT).show();
                            }

                            events.remove(getAdapterPosition());
                            if(events.size() == 0) {
                                View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                                View v = rootView.findViewById(R.id.event_instruction);
                                v.setVisibility(View.VISIBLE);
                            }
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    });
                    androidx.appcompat.app.AlertDialog d = dialog.create();
                    d.show();
                    d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                }
            });
        }
    }

    public boolean updateEventInCalendar(event e, Calendar cal) {

        contentResolver = mContext.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events._ID, e.getEventId());
        contentValues.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
        contentValues.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        String where = "_id =" + e.getEventId() + " and " + CalendarContract.Events.CALENDAR_ID + "=" + 1;

        contentResolver.update(CalendarContract.Events.CONTENT_URI, contentValues, where, null);

        /*if(uri != 0) {
            Toast.makeText(mContext, "Updated Events!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Unknown error occurred! " + e.getEventId(), Toast.LENGTH_LONG).show();
        }*/

        where = CalendarContract.Reminders.EVENT_ID + "=" +  e.getEventId();

        contentValues = new ContentValues();
        contentValues.put(CalendarContract.Reminders.MINUTES, 10);
        contentValues.put(CalendarContract.Reminders.EVENT_ID, e.getEventId());
        contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        int uri = contentResolver.update(CalendarContract.Reminders.CONTENT_URI, contentValues, where, null);

        /*if(uri != 0) {
            Toast.makeText(mContext, "Updated Reminder!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Unknown error occurred!", Toast.LENGTH_LONG).show();
        }*/

        return (uri != 0);

    }
}
