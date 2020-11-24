package com.example.oops_project;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class categoryRecViewAdapter extends RecyclerView.Adapter<categoryRecViewAdapter.ViewHolder> {

    FloatingActionButton add;
    private ArrayList<category> categories = new ArrayList<>();
    private Context mContext;
    private FragmentManager fragmentManager;
    Toolbar toolbar;

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
        holder.nameCategory.setText(categories.get(position).getName());
        holder.desCategory.setText(categories.get(position).getShortDes());
        Glide.with(mContext).asBitmap().load(categories.get(position).getImageURL()).into(holder.imgCategory);
        holder.cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, categories.get(position).getName(), Toast.LENGTH_SHORT).show();
                toolbar.setTitle(categories.get(position).getName());
                fragmentManager.beginTransaction().replace(R.id.container_fragment, new frag_item(categories.get(position).getItems(), add)).addToBackStack(null).commit();
            }
        });
        holder.desCategory.setText(categories.get(position).getShortDes());

        if (categories.get(position).isExpanded()) {
            TransitionManager.beginDelayedTransition((holder.cardCategory));
            holder.viewMore.setText("VIEW LESS");
            holder.desCategory.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition((holder.cardCategory));
            holder.viewMore.setText("VIEW MORE");
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

        private ImageView imgCategory;
        private TextView nameCategory;
        private TextView desCategory;
        private MaterialCardView cardCategory;
        private RelativeLayout shortCategory;
        private MaterialButton viewMore;
        private MaterialButton deleteCategory;
        private MaterialButton imgUpdate;

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

                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext).setTitle("Delete " + categories.get(getAdapterPosition()).getName()).setMessage("Are you sure you want to delete this category?");
                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, categories.get(getAdapterPosition()).getName() + " deleted!", Toast.LENGTH_SHORT).show();
                            categories.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

        }
    }
}
