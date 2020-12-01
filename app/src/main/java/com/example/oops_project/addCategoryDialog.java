package com.example.oops_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class addCategoryDialog extends DialogFragment {

    private transferCall transferCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SmallDialog);
    }

    public void setTransferCall(addCategoryDialog.transferCall transferCall) {
        this.transferCall = transferCall;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_category, container, false);

        EditText nameCategory = view.findViewById(R.id.edtTextNameCategory);
        EditText desCategory = view.findViewById(R.id.edtTextDesCategory);

        view.findViewById(R.id.CategoryCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.CategorySaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameCategory.getText().toString().trim()))
                    nameCategory.setError("Name cannot be empty!");
                else if (TextUtils.isEmpty(desCategory.getText().toString().trim()))
                    desCategory.setError("Description cannot be empty!");
                else {
                    transferCall.onSaveCategory(nameCategory.getText().toString().trim(), desCategory.getText().toString().trim());
                    dismiss();
                }
            }
        });

        return view;
    }

    public interface transferCall {
        void onSaveCategory(String name, String des);
    }
}
