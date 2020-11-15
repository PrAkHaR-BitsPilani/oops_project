package com.example.oops_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class categoryRecViewAdapter extends RecyclerView.Adapter<categoryRecViewAdapter.ViewHolder> {

    private ArrayList<category> categories = new ArrayList<>();
    private Context mContext;
    private FragmentManager fragmentManager;

    public categoryRecViewAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view, parent , false);
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
                fragmentManager.beginTransaction().replace(R.id.container_fragment, new frag_item(categories.get(position).getItems())).commit();
            }
        });
        holder.desCategory.setText(categories.get(position).getShortDes());

        if(categories.get(position).isExpanded())
        {
            TransitionManager.beginDelayedTransition((holder.cardCategory));
            holder.viewMore.setText("VIEW LESS");
            holder.desCategory.setVisibility(View.VISIBLE);
        }
        else{
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgCategory;
        private TextView nameCategory;
        private TextView desCategory;
        private MaterialCardView cardCategory;
        private RelativeLayout shortCategory;
        private MaterialButton viewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.categoryParent);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            nameCategory = itemView.findViewById(R.id.nameCategory);
            desCategory = itemView.findViewById(R.id.desCategory);
            shortCategory = itemView.findViewById(R.id.shortCategory);
            viewMore = itemView.findViewById(R.id.viewMore);

            viewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    category cnt = categories.get(getAdapterPosition());
                    cnt.setExpanded(!cnt.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }
}
