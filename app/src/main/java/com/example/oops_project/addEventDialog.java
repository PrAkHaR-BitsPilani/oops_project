package com.example.oops_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class addEventDialog extends DialogFragment {

    int timePickerHour , timePickerMinute;
    private transferCall transferCall;

    public void setTransferCall(addEventDialog.transferCall transferCall) {
        this.transferCall = transferCall;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL , R.style.SmallDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_event , container , false);

        EditText nameEvent = view.findViewById(R.id.edtTextNameEvent);
        EditText desEvent = view.findViewById(R.id.edtTextDesEvent);
        TextView timeEvent = view.findViewById(R.id.textViewTimeEvent);
        TextView dateEvent = view.findViewById(R.id.textViewDateEvent);

        dateEvent.setText(DateFormat.format("dd/MM/yyyy",Calendar.getInstance()));


        timeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        timePickerHour = hourOfDay;
                        timePickerMinute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,timePickerHour,timePickerMinute);
                        timeEvent.setText(DateFormat.format("hh:mm:aa",calendar));
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),false
                );
                timePickerDialog.updateTime(timePickerHour , timePickerMinute);
                timePickerDialog.show();
                timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.pink));
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.pink));
            }
        });

        dateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR , year);
                        calendar.set(Calendar.MONTH , month);
                        calendar.set(Calendar.DAY_OF_MONTH , dayOfMonth);
                        dateEvent.setText(DateFormat.format("dd/MM/yyyy",calendar));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity() , listener , calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.pink));
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.pink));
            }
        });

        view.findViewById(R.id.EventEditCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.EventEditSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nameEvent.getText().toString().trim()))
                    nameEvent.setError("Name cannot be empty");
                else if(TextUtils.isEmpty(desEvent.getText().toString().trim()))
                    desEvent.setError("Description cannot be empty");
                else{
                    transferCall.onSaveEvent(
                            nameEvent.getText().toString().trim(),
                            desEvent.getText().toString().trim(),
                            dateEvent.getText().toString().trim(),
                            timeEvent.getText().toString().trim()
                    );
                    dismiss();
                }
            }
        });

        return view;
    }

    public interface transferCall{
        void onSaveEvent(String name, String des, String date, String time);
    }
}
