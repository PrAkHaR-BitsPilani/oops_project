package com.example.oops_project;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class itemRecViewAdapter extends RecyclerView.Adapter<itemRecViewAdapter.ViewHolder> {

    private ArrayList<item> items = new ArrayList<>();
    private Context mContext;
    private FragmentManager fragmentManager;

    public itemRecViewAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new itemRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameItem.setText(items.get(position).getName());
        holder.quantityItem.setText("Quantity: " + items.get(position).getQuantity());
        holder.priceItem.setText("Price: " + items.get(position).getPrice() + "");
        Glide.with(mContext).asBitmap().load(items.get(position).getImgURI()).into(holder.imgItem);
        holder.shareItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Sharing in progress...", Toast.LENGTH_SHORT).show();
            }
        });
        holder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItemDialog dialog = new editItemDialog();
                dialog.setTransferCall(new editItemDialog.transferCall() {
                    @Override
                    public void onSaveEditItem(String price, String quantity) {
                        items.get(position).setPrice(price);
                        items.get(position).setQuantity(quantity);
                        notifyItemChanged(position);
                        Toast.makeText(mContext, "Changes saved!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(fragmentManager, "Editing item...");
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext).setTitle("Delete " + items.get(holder.getAdapterPosition()).getName()).setMessage("Are you sure you want to delete this item?");
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, items.get(holder.getAdapterPosition()).getName() + " deleted!", Toast.LENGTH_SHORT).show();
                        items.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
                dialog.show();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameItem;
        private ImageView imgItem;
        private TextView quantityItem, priceItem;
        private MaterialButton shareItem, editItem, deleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameItem = itemView.findViewById(R.id.nameItem);
            imgItem = itemView.findViewById(R.id.imgItem);
            quantityItem = itemView.findViewById(R.id.quantityItem);
            priceItem = itemView.findViewById(R.id.priceItem);
            shareItem = itemView.findViewById(R.id.shareItem);
            editItem = itemView.findViewById(R.id.editItem);
            deleteItem = itemView.findViewById(R.id.deleteItem);
        }
    }
}
