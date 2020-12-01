package com.example.oops_project;

import android.app.Activity;
import android.content.Context;
import android.database.DatabaseUtils;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private final DatabaseHandler db;
    private final Context mContext;
    private final FragmentManager fragmentManager;
    private View gItemView;

    public ToDoAdapter(DatabaseHandler db, Context context, FragmentManager fragmentManager) {
        this.db = db;
        this.mContext = context;
        this.fragmentManager = fragmentManager;
    }

    public void setTodoList(List<ToDoModel> todoList) {
        this.todoList = todoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        gItemView = itemView;
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        final ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        if(holder.task.isChecked()) {
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    db.updateStatus(item.getId(), 0);
                    holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
//changing context of activity to context of fragment
//      return activity;
        return mContext;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    // this method deletes the task in database, also notifies the recyclerView
    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);

        if((int) (DatabaseUtils.longForQuery(db.getReadableDatabase(), "SELECT COUNT (*) FROM todo", null)) == 0) {
            View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
            View v = rootView.findViewById(R.id.task_instruction);
            v.setVisibility(View.VISIBLE);
        }


        notifyItemRemoved(position);
    }

    // this method shows the items after any changes
    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask(this, gItemView);
        fragment.setArguments(bundle);
        fragment.show(fragmentManager, AddNewTask.TAG);
//        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
