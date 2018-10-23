package org.yanning.recyclerwheelpicker;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 纯文字的选择器
 *
 * @param <T>
 */
public abstract class TextViewWheelAdapter<T> extends WheelAdapter<T> {
    private static final int textId = 0x3467;

    public static int getTextId() {
        return textId;
    }

    @Override
    protected WheelViewHolder onWheelCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = new LinearLayout(parent.getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(getPicker().mWidth, getItemHeight()));
        TextView textView = new TextView(parent.getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.argb(255, 90, 90, 90));
        textView.setPadding(20, 20, 20, 20);
        textView.setId(textId);
        linearLayout.addView(textView);
        return new WheelViewHolder(linearLayout) {
        };
    }

    @Override
    protected void onWheelBindViewHolder(@NonNull WheelViewHolder holder, int position, T t) {
        TextView textView = holder.itemView.findViewById(textId);
        textView.setText(getWheelItemName(position, t));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicker().onClickItemView(v);
            }
        });
    }

    @Override
    protected void onWheelScrollTranslate(RecyclerView.ViewHolder holder, float progress) {
        TextView textView = holder.itemView.findViewById(textId);
        textView.setScaleY(Math.abs(progress) * 0.6f + 0.4f);
        textView.setScaleX(Math.abs(progress) * 0.6f + 0.4f);
        textView.setTextColor(Color.argb((int) (120 + 125 * Math.abs(progress)), 90, 90, 90));
        textView.setAlpha(150 + 105 * Math.abs(progress));
    }

    /**
     * @param position
     * @param t
     * @return
     */
    protected abstract String getWheelItemName(int position, T t);
}