package org.yanning.recyclerwheelpicker;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 基于recyclerview的滚轮选择器,支持自定义view,自定义滚动特效,自定义滚轮样式,万条数据也不会卡顿,流畅滚动<br>
 * <a href="https://github.com/NingOpenSource/RecyclerWheelPicker">https://github.com/NingOpenSource/RecyclerWheelPicker</a>
 * create by yanning
 *
 * @param <T>
 */
public class RecyclerWheelPicker<T> extends LinearLayout {
    public RecyclerWheelPicker(Context context) {
        super(context);
        initRoot();
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRoot();
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecyclerWheelPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initRoot();
    }

    private RecyclerWheelPickerCoreView<T> coreView;

    private final void initRoot() {
        coreView = new RecyclerWheelPickerCoreView<>(getContext());
        post(new Runnable() {
            @Override
            public void run() {
                coreView.mWidth = getMeasuredWidth();
                addView(coreView);
            }
        });
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        setCoreViewLayoutParams(params);
    }

    /**
     * 设置选中区域的LayoutParams
     *
     * @param params
     */
    private final void setCoreViewLayoutParams(ViewGroup.LayoutParams params) {
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams layoutParams = new LayoutParams(params);
        layoutParams.width = LayoutParams.MATCH_PARENT;
        coreView.setLayoutParams(layoutParams);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setCoreViewLayoutParams(params);
    }

    public WheelAdapter<T> getAdapter() {
        return coreView.adapter;
    }

    /**
     * 设置选中区域顶部的蒙层
     *
     * @param selectedTopAreaDrawer
     */
    public void setSelectedTopAreaDrawer(AreaDrawer selectedTopAreaDrawer) {
        coreView.setSelectedTopAreaDrawer(selectedTopAreaDrawer);
    }

    /**
     * 设置选中区域底部的蒙层
     *
     * @param selectedBottomAreaDrawer
     */
    public void setSelectedBottomAreaDrawer(AreaDrawer selectedBottomAreaDrawer) {
        coreView.setSelectedBottomAreaDrawer(selectedBottomAreaDrawer);
    }

    /**
     * 设置选中区域的蒙层，比如上下各有一个灰色的分割线
     *
     * @param selectedAreaDrawer
     */
    public void setSelectedAreaDrawer(AreaDrawer selectedAreaDrawer) {
        coreView.setSelectedAreaDrawer(selectedAreaDrawer);
    }

    /**
     * 设置选中区域高度，建议设计的每个滚动元素的高度保持一致
     *
     * @param selectedAreaHeight
     */
    public void setSelectedAreaHeight(int selectedAreaHeight) {
        coreView.setSelectedAreaHeight(selectedAreaHeight);
    }


    public void setAdapter(WheelAdapter<T> adapter) {
        coreView.setAdapter(adapter);
    }


    /**
     * 设置可见区域最多显示的元素个数，不足奇数将自动转换为奇数
     *
     * @param maxShowSize
     */
    public void setMaxShowSize(int maxShowSize) {
        coreView.setMaxShowSize(maxShowSize);
    }

    /**
     * 设置默认值,必须在设置adapter之后调用
     *
     * @param t
     */
    public void setDefaultValue(T t) {
        if (getAdapter() == null) {
            throw new RuntimeException("ERROR:\nsetDefaultValue()必须在设置setAdapter()之后调用\n" +
                    "'adapter' is Null,please use setAdapter() before.");
        }
        coreView.setDefaultValue(t);
    }

    /**
     * 获取选中区域对应的索引
     *
     * @return
     */
    public int getSelectedIndex() {
        return coreView.getSelectedIndex();
    }

    /**
     * 获取选中区域对应的值
     *
     * @return
     */
    public T getSelected() {
        return coreView.getSelected();
    }

    /**
     * 滚动到
     *
     * @param position
     */
    public void scrollToPosition(int position) {
//        super.scrollToPosition(position);
        coreView.scrollToPosition(position);
    }
}