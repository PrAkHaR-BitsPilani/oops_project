package com.example.oops_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class itemRecViewAdapter extends RecyclerView.Adapter<itemRecViewAdapter.ViewHolder> {

    private ArrayList<item>items = new ArrayList<>();
    private Context mContext;

    public itemRecViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent , false);
        return new itemRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameItem.setText(items.get(position).getName());
        holder.quantityItem.setText("Quantity "+items.get(position).getQuantity());
        holder.priceItem.setText("Price "+items.get(position).getPrice() + "");
        Glide.with(mContext).asBitmap().load(items.get(position).getImgURI()).into(holder.imgItem);
        holder.shareItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Sharing in progress", Toast.LENGTH_SHORT).show();
            }
        });
        holder.incItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).setQuantity(items.get(position).getQuantity() + 1);
                notifyItemChanged(position);
            }
        });
        holder.decItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).setQuantity(java.lang.Math.max(0 , items.get(position).getQuantity() - 1));
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameItem;
        private ImageView imgItem;
        private TextView quantityItem , priceItem;
        private MaterialButton incItem, decItem, shareItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameItem = itemView.findViewById(R.id.nameItem);
            imgItem = itemView.findViewById(R.id.imgItem);
            quantityItem = itemView.findViewById(R.id.quantityItem);
            priceItem = itemView.findViewById(R.id.priceItem);
            incItem = itemView.findViewById(R.id.incItem);
            decItem = itemView.findViewById(R.id.decItem);
            shareItem = itemView.findViewById(R.id.shareItem);
        }
    }
}
