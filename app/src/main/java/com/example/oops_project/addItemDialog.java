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

public class addItemDialog extends DialogFragment {

    private transferCall transferCall;

    public void setTransferCall(addItemDialog.transferCall transferCall) {
        this.transferCall = transferCall;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SmallDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_item, container, false);

        EditText nameItem = view.findViewById(R.id.edtTextNameItem);
        EditText quantityItem = view.findViewById(R.id.edtTextQuantityItem);
        EditText priceItem = view.findViewById(R.id.edtTextPriceItem);

        view.findViewById(R.id.ItemCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.ItemSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameItem.getText().toString().trim()))
                    nameItem.setError("Name cannot be empty!");
                else if (TextUtils.isEmpty(quantityItem.getText().toString().trim()))
                    quantityItem.setError("Quantity cannot be empty!");
                else if (TextUtils.isEmpty(priceItem.getText().toString().trim()))
                    priceItem.setError("Price cannot be empty!");
                else {
                    transferCall.onSaveItem(nameItem.getText().toString().trim(), priceItem.getText().toString().trim(), quantityItem.getText().toString().trim());
                    dismiss();
                }
            }
        });

        return view;
    }

    public interface transferCall {
        public void onSaveItem(String name, String price, String quantity);
    }
}
