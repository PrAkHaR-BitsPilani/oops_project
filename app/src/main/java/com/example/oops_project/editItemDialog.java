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

public class editItemDialog extends DialogFragment {

    private transferCall transferCall;

    public void setTransferCall(editItemDialog.transferCall transferCall) {
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
        View view = inflater.inflate(R.layout.edit_item, container, false);

        EditText quantityItem = view.findViewById(R.id.edtTextQuantityItemEdit);
        EditText priceItem = view.findViewById(R.id.edtTextPriceItemEdit);

        view.findViewById(R.id.ItemEditCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.ItemEditSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(quantityItem.getText().toString().trim()))
                    quantityItem.setError("Quantity cannot be empty!");
                else if (TextUtils.isEmpty(priceItem.getText().toString().trim()))
                    priceItem.setError("Price cannot be empty!");
                else {
                    transferCall.onSaveEditItem(priceItem.getText().toString().trim(), quantityItem.getText().toString().trim());
                    ;
                    dismiss();
                }

            }
        });

        return view;
    }

    public interface transferCall {
        public void onSaveEditItem(String price, String quantity);
    }

}
