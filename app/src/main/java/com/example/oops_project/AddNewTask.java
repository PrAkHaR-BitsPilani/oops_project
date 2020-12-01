package com.example.oops_project;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class AddNewTask extends DialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSavedButton;
    private final ToDoAdapter tasksAdapter;
    private final View parentView;

    private DatabaseHandler db;

    public AddNewTask(ToDoAdapter tasksAdapter, View parentView) {
        this.tasksAdapter = tasksAdapter;
        this.parentView = parentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NORMAL, R.style.DialogStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // editing getView() to view
        newTaskText = Objects.requireNonNull(view).findViewById(R.id.newTaskText);
        newTaskSavedButton = getView().findViewById(R.id.newTaskButton);
//        newTaskSavedButton = view.findViewById(R.id.newTaskButton);

        boolean isUpdate = false;

//      cosmetics of typing stuff in the bottomsheetdialog
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            assert task != null;
            if (task.length() > 0) {
                newTaskSavedButton.setTextColor(ContextCompat.getColor(getContext(), R.color.pink));
            }
        }

        db = new DatabaseHandler(getContext());
        db.openDatabase();

        //enabling and color changing of save button
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim()) || s.toString() == null) {
                    newTaskSavedButton.setEnabled(false);
                    newTaskSavedButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSavedButton.setEnabled(true);
                    newTaskSavedButton.setTextColor(ContextCompat.getColor(getContext(), R.color.pink));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;

        newTaskSavedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString().trim();
                if (TextUtils.isEmpty(text) || text == null) {
                    return;
                }
                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);

                    tasksAdapter.setTodoList(db.getAllTasks());
                    tasksAdapter.notifyDataSetChanged();

                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    db.insertTask(task);

                    if((int) (DatabaseUtils.longForQuery(db.getReadableDatabase(), "SELECT COUNT (*) FROM todo", null)) == 0) {
                        parentView.findViewById(R.id.task_instruction).setVisibility(View.VISIBLE);
                    } else {
                        parentView.findViewById(R.id.task_instruction).setVisibility(View.GONE);
                    }

                    tasksAdapter.setTodoList(db.getAllTasks());
                    tasksAdapter.notifyDataSetChanged();
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        tasksAdapter.setTodoList(db.getAllTasks());
        tasksAdapter.notifyDataSetChanged();

        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);


        }
    }
}

