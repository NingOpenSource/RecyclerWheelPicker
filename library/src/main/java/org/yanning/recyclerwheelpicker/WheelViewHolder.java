package org.yanning.recyclerwheelpicker;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

public class WheelViewHolder extends RecyclerView.ViewHolder {

    public WheelViewHolder(View itemView) {
        super(itemView);
    }

    protected final WheelViewHolder setItemLayoutParameter(int height) {
        if (itemView.getLayoutParams() == null) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height));
        } else {
            itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
            itemView.getLayoutParams().height = height;
        }
        return this;
    }

    private final WheelViewHolder setBackgroundColor(@ColorInt int color) {
        itemView.setBackgroundColor(color);
        return this;
    }
}