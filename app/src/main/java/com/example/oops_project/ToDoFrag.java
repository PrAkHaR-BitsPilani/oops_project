package com.example.oops_project;

import android.content.DialogInterface;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class ToDoFrag extends Fragment implements DialogCloseListener {

    FloatingActionButton add_task;
    //adding recyclerview in the frag
    private RecyclerView tasksrecyclerview;
    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> tasklist;
    private DatabaseHandler db;
    private TextView inst;

    public ToDoFrag(FloatingActionButton add) {
        this.add_task = add;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflating the layout for this fragment
        View view = inflater.inflate(R.layout.todo_fragment, container, false);

        //initialize the db class week latest:changed getActivity to getContext
        db = new DatabaseHandler(getActivity());
        db.openDatabase();
        inst = view.findViewById(R.id.task_instruction);



        //creating and setting the recyclerview
        tasksAdapter = new ToDoAdapter(db, getActivity(), getFragmentManager());
        tasksrecyclerview = view.findViewById(R.id.tasksRecyclerView);
        tasksrecyclerview.setAdapter(tasksAdapter);
        tasksrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ToDoTouchHelper(tasksAdapter, getActivity()));
        itemTouchHelper.attachToRecyclerView(tasksrecyclerview);    //last line entered
        tasklist = db.getAllTasks();    //loading the stored data of tasks
        Collections.reverse(tasklist);
        tasksAdapter.setTasks(tasklist);

        if((int) (DatabaseUtils.longForQuery(db.getReadableDatabase(), "SELECT COUNT (*) FROM todo", null)) == 0) {
            inst.setVisibility(View.VISIBLE);
        }

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AddNewTask(tasksAdapter, view).show(getFragmentManager(), AddNewTask.TAG);
            }
        });

        return view;
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        tasklist = db.getAllTasks();
        Collections.reverse(tasklist);
        tasksAdapter.setTasks(tasklist);
        tasksAdapter.notifyDataSetChanged();
    }
}

