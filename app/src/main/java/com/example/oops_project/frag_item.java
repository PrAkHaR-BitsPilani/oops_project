package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class frag_item extends Fragment implements IOnBackPressed {

    ArrayList<item> items = new ArrayList<>();
    FloatingActionButton add_item;
    private RecyclerView itemRecView;
    private itemRecViewAdapter adapter;
    private transferCall transferCall;
    private int categoryId;

    public void setTransferCall(frag_item.transferCall transferCall) {
        this.transferCall = transferCall;
    }

    public frag_item(ArrayList<item> items, FloatingActionButton add, int categoryId) {
        this.items = items;
        this.add_item = add;
        this.categoryId = categoryId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        itemRecView = view.findViewById(R.id.itemRecView);
        adapter = new itemRecViewAdapter(getActivity(), getFragmentManager());
        adapter.setTransferCall(new itemRecViewAdapter.transferCall() {
            @Override
            public void imageUploadItem(itemRecViewAdapter adapter, int pos) {
                transferCall.imageUploadItem(adapter, pos);
            }
        });
        itemRecView.setAdapter(adapter);
        itemRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setItems(items);

        TextView inst = view.findViewById(R.id.item_instruction);
        if(items.size() == 0)inst.setVisibility(View.VISIBLE);else inst.setVisibility(View.GONE);

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog dialog = new addItemDialog();
                dialog.setTransferCall(new addItemDialog.transferCall() {
                    @Override
                    public void onSaveItem(String name, String price, String quantity) {
                        String defImg = "android.resource://com.example.oops_project/" + R.drawable.default_image;
                        items.add(new item("" + items.size(), categoryId, name, price, quantity, defImg, "default_image"));
                        adapter.notifyItemInserted(items.size());
                        inst.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), name + " added!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(getFragmentManager(), "Adding item...");
            }
        });

        return view;
    }

    public interface transferCall {
        void imageUploadItem(itemRecViewAdapter adapter, int pos);
    }

    public boolean onBackPressed() {
        return false;
    }

}
