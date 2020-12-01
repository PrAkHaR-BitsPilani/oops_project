package com.example.oops_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class itemRecViewAdapter extends RecyclerView.Adapter<itemRecViewAdapter.ViewHolder> {
    private final Context mContext;
    private final FragmentManager fragmentManager;
    String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/com.example.oops_project/files/users/" + Dashboard.uID
            + "/profileImg.jpg";
    private ArrayList<item> items = new ArrayList<>();
    private transferCall transferCall;

    public itemRecViewAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    public ArrayList<item> getItems() {
        return items;
    }

    public void setItems(ArrayList<item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setTransferCall(itemRecViewAdapter.transferCall transferCall) {
        this.transferCall = transferCall;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new itemRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imgUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferCall.imageUploadItem(itemRecViewAdapter.this, holder.getAdapterPosition());
            }
        });

        holder.nameItem.setText(items.get(position).getName());
        holder.quantityItem.setText("Quantity: " + items.get(position).getQuantity());
        holder.priceItem.setText("Price: " + items.get(position).getPrice() + "");

        String imgUri = items.get(position).getImgURI();

        File file = new File(imgUri);
        if(!file.exists()) {
            imgUri = "android.resource://com.example.oops_project/" + R.drawable.default_image;
            items.get(position).setImgURI(imgUri);
            items.get(position).setShareURI("default_image");
        }

        Glide.with(mContext)
                .asBitmap()
                .load(items.get(position).getImgURI())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.imgItem);

        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             items.get(holder.getAdapterPosition()).setExpanded(!items.get(holder.getAdapterPosition()).isExpanded());
             notifyItemChanged(holder.getAdapterPosition());
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
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext, R.style.MyDialogTheme);
                dialog.setTitle("Delete " + items.get(holder.getAdapterPosition()).getName());
                dialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to delete this item?</font>"));
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, items.get(holder.getAdapterPosition()).getName() + " deleted!", Toast.LENGTH_SHORT).show();
                        items.remove(holder.getAdapterPosition());
                        if(items.size() == 0) {
                            View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                            View v = rootView.findViewById(R.id.item_instruction);
                            v.setVisibility(View.VISIBLE);
                        }
                        notifyItemRemoved(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
                androidx.appcompat.app.AlertDialog d = dialog.create();
                d.show();
                d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            }
        });

        if (items.get(position).isExpanded()) {
            TransitionManager.beginDelayedTransition((holder.materialCardView));
            holder.imgItem.setVisibility(View.VISIBLE);
            holder.imgUpdateItem.setVisibility(View.VISIBLE);

        } else {
            TransitionManager.beginDelayedTransition((holder.materialCardView));
            holder.imgItem.setVisibility(View.GONE);
            holder.imgUpdateItem.setVisibility(View.GONE);            
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface transferCall {
        void imageUploadItem(itemRecViewAdapter adapter, int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameItem;
        private final ImageView imgItem;
        private final TextView quantityItem;
        private final TextView priceItem;
        private final MaterialButton shareItem;
        private final MaterialButton editItem;
        private final MaterialButton deleteItem;
        private final MaterialButton imgUpdateItem;
        private final MaterialCardView materialCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameItem = itemView.findViewById(R.id.nameItem);
            imgItem = itemView.findViewById(R.id.imgItem);
            quantityItem = itemView.findViewById(R.id.quantityItem);
            priceItem = itemView.findViewById(R.id.priceItem);
            shareItem = itemView.findViewById(R.id.shareItem);
            editItem = itemView.findViewById(R.id.editItem);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            imgUpdateItem = itemView.findViewById(R.id.imgUpdateItem);
            materialCardView = itemView.findViewById(R.id.itemParent);

            shareItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data = "Name: " + items.get(getAdapterPosition()).getName() + "\n" +
                            "Quantity: " + items.get(getAdapterPosition()).getQuantity() + "\n" +
                            "Price: " + items.get(getAdapterPosition()).getPrice();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, data);

                    if (!items.get(getAdapterPosition()).getShareURI().equals("default_image")) {
                        Uri imgUri = FileProvider.getUriForFile(mContext, "com.mydomain.fileprovider", new File(items.get(getAdapterPosition()).getShareURI()));
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                        sendIntent.setType("image/*");
                    } else {
                        sendIntent.setType("text/*");
                    }

                    Intent shareIntent = Intent.createChooser(sendIntent, "Select the app to share to: ");
                    mContext.startActivity(shareIntent);
                }
            });
        }
    }
}
