package org.yanning.recyclerwheelpicker;

import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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