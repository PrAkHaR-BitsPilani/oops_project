package com.example.oops_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class categoryRecViewAdapter extends RecyclerView.Adapter<categoryRecViewAdapter.ViewHolder> implements Filterable {

    FloatingActionButton add;
    private ArrayList<category> categories = new ArrayList<>();
    private ArrayList<category> categoriesFiltered;
    private final Context mContext;
    private final FragmentManager fragmentManager;
    Toolbar toolbar;
    private transferCall transferCall;
    private Filter categoryFilter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            List<category> filteredCategories = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
                filteredCategories.addAll(categories);
            else{
                String pattern = constraint.toString().toLowerCase().trim();
                for(category e : categories){
                    if(e.getName().toLowerCase().contains(pattern))
                        filteredCategories.add(e);
                }
            }
            Filter.FilterResults results = new Filter.FilterResults();
            results.values = filteredCategories;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                categories.clear();
                categories.addAll((ArrayList<category>)results.values);
                notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return categoryFilter;
    }



    public void setTransferCall(categoryRecViewAdapter.transferCall transferCall) {
        this.transferCall = transferCall;
    }

    public ArrayList<category> getCategories() {
        return categories;
    }

    public categoryRecViewAdapter(Context mContext, FragmentManager fragmentManager, FloatingActionButton add, Toolbar toolbar) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.add = add;
        this.toolbar = toolbar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferCall.imageUploadCategory(categoryRecViewAdapter.this, holder.getAdapterPosition());
            }
        });
        holder.nameCategory.setText(categories.get(position).getName());
        holder.desCategory.setText(categories.get(position).getShortDes());

        String imgUri = categories.get(position).getImageURL();

        File file = new File(imgUri);
        if(!file.exists()) {
            imgUri = "android.resource://com.example.oops_project/" + R.drawable.default_image;
            categories.get(position).setImageURL(imgUri);
        }

        Glide.with(mContext).asBitmap()
                .load(imgUri)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.imgCategory);

        /*holder.cardCategory.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(holder.imgCategory.getVisibility() == View.VISIBLE) {
                    holder.imgCategory.setVisibility(View.GONE);
                    holder.imgUpdate.setVisibility(View.GONE);
                } else {
                    holder.imgCategory.setVisibility(View.VISIBLE);
                    holder.imgUpdate.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });*/

        holder.cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, categories.get(position).getName(), Toast.LENGTH_SHORT).show();
                toolbar.setTitle(categories.get(position).getName());
                frag_item frag_item = new frag_item(categories.get(position).getItems(), add, categories.get(position).getId());
                frag_item.setTransferCall(new frag_item.transferCall() {
                    @Override
                    public void imageUploadItem(itemRecViewAdapter adapter, int pos) {
                        transferCall.imageUploadItem(adapter, pos);
                    }
                });
                fragmentManager.beginTransaction().replace(R.id.container_fragment, frag_item).addToBackStack(null).commit();
            }
        });
        holder.desCategory.setText(categories.get(position).getShortDes());

        if (categories.get(position).isExpanded()) {
            TransitionManager.beginDelayedTransition((holder.cardCategory));
            holder.viewMore.setText("VIEW LESS");
            holder.imgCategory.setVisibility(View.VISIBLE);
            holder.imgUpdate.setVisibility(View.VISIBLE);
            holder.desCategory.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition((holder.cardCategory));
            holder.viewMore.setText("VIEW MORE");
            holder.imgCategory.setVisibility(View.GONE);
            holder.imgUpdate.setVisibility(View.GONE);
            holder.desCategory.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(ArrayList<category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgCategory;
        private final TextView nameCategory;
        private final TextView desCategory;
        private final MaterialCardView cardCategory;
        private final RelativeLayout shortCategory;
        private final MaterialButton viewMore;
        private final MaterialButton deleteCategory;
        private final MaterialButton imgUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.categoryParent);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            nameCategory = itemView.findViewById(R.id.nameCategory);
            desCategory = itemView.findViewById(R.id.desCategory);
            shortCategory = itemView.findViewById(R.id.shortCategory);
            viewMore = itemView.findViewById(R.id.viewMore);
            imgUpdate = itemView.findViewById(R.id.imgUpdateCategory);

            viewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    category cnt = categories.get(getAdapterPosition());
                    cnt.setExpanded(!cnt.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            deleteCategory = itemView.findViewById(R.id.deleteCategory);
            deleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext, R.style.MyDialogTheme);
                    dialog.setTitle("Delete " + categories.get(getAdapterPosition()).getName());
                    dialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to delete this category?</font>"));
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, categories.get(getAdapterPosition()).getName() + " deleted!", Toast.LENGTH_SHORT).show();
                            categories.remove(getAdapterPosition());

                            if(categories.size() == 0) {
                                View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                                View v = rootView.findViewById(R.id.category_instruction);
                                v.setVisibility(View.VISIBLE);
                            }
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    });
                    androidx.appcompat.app.AlertDialog d = dialog.create();
                    d.show();
                    d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.blue));

                }
            });

        }
    }

    public interface transferCall {
        void imageUploadCategory(categoryRecViewAdapter adapter, int pos);
        void imageUploadItem(itemRecViewAdapter adapter, int pos);
    }
}
