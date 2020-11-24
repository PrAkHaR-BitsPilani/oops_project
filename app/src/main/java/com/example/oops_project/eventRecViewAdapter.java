package com.example.oops_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class eventRecViewAdapter extends RecyclerView.Adapter<eventRecViewAdapter.ViewHolder> {

    private ArrayList<event> events = new ArrayList<>();
    private Context mContext;

    public eventRecViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameEvent.setText(events.get(position).getName());
        holder.desEvent.setText(events.get(position).getDescription());
        holder.cardEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TransitionManager.beginDelayedTransition((holder.cardEvent));
                if (holder.venueEvent.getVisibility() == View.GONE)
                    holder.venueEvent.setVisibility(View.VISIBLE);
                else
                    holder.venueEvent.setVisibility(View.GONE);
            }
        });
    }

    public void delete(int pos) {

    }

    public void add(int pos) {

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(ArrayList<event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameEvent, desEvent, venueEvent;
        private MaterialCardView cardEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameEvent = itemView.findViewById(R.id.nameEvent);
            desEvent = itemView.findViewById(R.id.desEvent);
            venueEvent = itemView.findViewById(R.id.venueEvent);
            cardEvent = itemView.findViewById(R.id.eventParent);
        }
    }
}
