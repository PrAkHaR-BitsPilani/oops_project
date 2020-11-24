package com.example.oops_project;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GenericTextWatcher implements TextWatcher {
    private final View view;
    private final EditText[] otpArray;

    public GenericTextWatcher(View view, EditText[] otpArray) {
        this.view = view;
        this.otpArray = otpArray;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();

        int id = view.getId();
        if (id == otpArray[0].getId()) {
            if (text.length() == 1) {
                otpArray[1].requestFocus();
            } else if (text.length() == 0) {
                for (int i = 1; i < 6; i++) {
                    otpArray[i].setText("", TextView.BufferType.EDITABLE);
                }
            }
        } else if (id == otpArray[1].getId()) {
            if (text.length() == 1) {
                otpArray[2].requestFocus();
            } else if (text.length() == 0) {
                for (int i = 2; i < 6; i++) {
                    otpArray[i].setText("", TextView.BufferType.EDITABLE);
                }
            }
        } else if (id == otpArray[2].getId()) {
            if (text.length() == 1) {
                otpArray[3].requestFocus();
            } else if (text.length() == 0) {
                for (int i = 3; i < 6; i++) {
                    otpArray[i].setText("", TextView.BufferType.EDITABLE);
                }
            }
        } else if (id == otpArray[3].getId()) {
            if (text.length() == 1) {
                otpArray[4].requestFocus();
            } else if (text.length() == 0) {
                for (int i = 4; i < 6; i++) {
                    otpArray[i].setText("", TextView.BufferType.EDITABLE);
                }
            }
        } else if (id == otpArray[4].getId()) {
            if (text.length() == 1) {
                otpArray[5].requestFocus();
            } else if (text.length() == 0) {
                for (int i = 5; i < 6; i++) {
                    otpArray[i].setText("", TextView.BufferType.EDITABLE);
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}