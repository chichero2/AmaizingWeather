package com.amaizzzing.amaizingweather;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerClickListener implements RecyclerView.OnItemTouchListener {
    View beforeView;
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener gestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            };

    public RecyclerClickListener(Context context) {
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (gestureDetector.onTouchEvent(e)) {

            View clickedChild = rv.findChildViewUnder(e.getX(), e.getY());
            if (clickedChild != null) {
                clickedChild.dispatchTouchEvent(e);
                int clickedPosition = rv.getChildAdapterPosition(clickedChild);
                onItemClick(rv, clickedChild, clickedPosition);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    public abstract void onItemClick(RecyclerView recyclerView, View itemView, int position);


}
