package com.example.oops_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public ToDoTouchHelper(ToDoAdapter adapter) {
        //enabling both left and right swipe functions
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    //not needed
    @Override
    public boolean onMove(RecyclerView recyclerview, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    // what happens on swipe
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {

//            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");
            builder.setMessage("Delete this task?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteItem(position);
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            adapter.editItem(position);
        }
    }

    //displaying icon while swiping right or left
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        //swiping in left direction
        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
            background = new ColorDrawable((ContextCompat.getColor(adapter.getContext(), R.color.purple_200)));
        } else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
            background = new ColorDrawable(Color.RED);
        }
        // making margins
        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) {//swiping right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) {//swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        icon.draw(c);
    }
}
