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

    public frag_item(ArrayList<item> items, FloatingActionButton add) {
        this.items = items;
        this.add_item = add;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        itemRecView = view.findViewById(R.id.itemRecView);
        adapter = new itemRecViewAdapter(getActivity(), getFragmentManager());
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
                        String defImgItemUri = "https://media.gettyimages.com/photos/closeup-of-multi-colored-toys-over-white-background-picture-id1094028428?k=6&m=1094028428&s=612x612&w=0&h=YDfxr7175ae4yi07DtWOcqtAqi9GUIthBHNZzAF4dO8=";
                        items.add(new item("" + items.size(), name, price, quantity, defImgItemUri));
                        adapter.notifyItemInserted(items.size());
                        inst.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), name + " added!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(getFragmentManager(), "Adding item...");
                ;
            }
        });

        return view;
    }

    public boolean onBackPressed() {
        return false;
    }

}
