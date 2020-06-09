package com.amaizzzing.amaizingweather.functions;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int hSpace;
    private int vSpace;

    public SpacesItemDecoration(int hSpace, int vSpace) {
        this.hSpace = hSpace;
        this.vSpace = vSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = hSpace;
        outRect.left = hSpace;
        outRect.top = vSpace;
        outRect.bottom = vSpace;
    }
}
