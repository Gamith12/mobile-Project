package com.luxevistaresort.Utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private final int dividerHeight;
    private final int margin;

    public DividerItemDecoration(int color, int height, int margin) {
        paint = new Paint();
        paint.setColor(color);
        dividerHeight = height;
        this.margin = margin; // Initialize the margin
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = dividerHeight + margin; // Add margin to the offset
        outRect.bottom = dividerHeight + margin; // Add margin to the offset
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft() + margin; // Apply margin to left
        int right = parent.getWidth() - parent.getPaddingRight() - margin; // Apply margin to right

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getTop() - params.topMargin - dividerHeight - margin; // Adjust top for margin
            int bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, paint);

            top = child.getBottom() + params.bottomMargin + margin; // Adjust top for margin
            bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, paint);
        }
    }
}