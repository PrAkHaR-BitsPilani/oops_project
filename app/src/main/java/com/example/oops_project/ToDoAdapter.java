package com.example.oops_project;

import android.content.Context;
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
    private DatabaseHandler db;
    private Context mContext;
    private FragmentManager fragmentManager;

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
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        final ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
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
        notifyItemRemoved(position);
    }

    // this method shows the items after any changes
    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask(this);
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
