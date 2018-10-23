package org.yanning.recyclerwheelpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基础的底层View，承载滚动选择的元素
 *
 * @param <T>
 */
class RecyclerWheelPickerCoreView<T> extends RecyclerView {
    public RecyclerWheelPickerCoreView(Context context) {
        super(context);
        init();
    }

    protected WheelAdapter<T> adapter;

    /**
     * 设置选中区域顶部的蒙层
     *
     * @param selectedTopAreaDrawer
     */
    public void setSelectedTopAreaDrawer(AreaDrawer selectedTopAreaDrawer) {
        this.selectedTopAreaDrawer = selectedTopAreaDrawer;
    }

    /**
     * 设置选中区域底部的蒙层
     *
     * @param selectedBottomAreaDrawer
     */
    public void setSelectedBottomAreaDrawer(AreaDrawer selectedBottomAreaDrawer) {
        this.selectedBottomAreaDrawer = selectedBottomAreaDrawer;
    }

    /**
     * 设置选中区域高度，建议设计的每个滚动元素的高度保持一致
     *
     * @param selectedAreaHeight
     */
    public void setSelectedAreaHeight(int selectedAreaHeight) {
        this.selectedAreaHeight = selectedAreaHeight;
        if (adapter != null) {
            adapter.setPicker(this, selectedAreaHeight, maxShowSize);
            adapter.notifyDataSetChanged();
        }
        if (getLayoutParams() == null) {
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxShowSize * selectedAreaHeight));
        } else {
            getLayoutParams().height = maxShowSize * selectedAreaHeight;
        }
    }

    protected final void onClickItemView(final View v) {
        final ViewHolder viewHolder = getChildViewHolder(v);
        if (viewHolder != null) {
            final int index = viewHolder.getAdapterPosition();
            int h = selectedAreaHeight * adapter.itemHeadOrFootSize - v.getTop();
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            valueAnimator = ValueAnimator.ofInt(v.getTop(), adapter.itemHeadOrFootSize * selectedAreaHeight);
            if (selectedAreaHeight > Math.abs(h)) {
                valueAnimator.setDuration(120);
            } else {
                valueAnimator.setDuration((long) (Math.abs(h) / Float.valueOf(selectedAreaHeight) * 90 + 30));
            }
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    linearLayoutManager.scrollToPositionWithOffset(index, (int) animation.getAnimatedValue());
                    refreshScrollTranslate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    selectedPosition = index;
                    int getSelectedIndex = getSelectedIndex();
                    if (selectedPosition >= 0 && getSelectedIndex >= 0)
                        adapter.onWheelSelected(viewHolder, getSelectedIndex, getSelected());
                }
            });
            valueAnimator.start();
        }
    }

    /**
     * {@hide}
     *
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof WheelAdapter) {
            setAdapter((WheelAdapter) adapter);
            return;
        }
        super.setAdapter(adapter);

    }

    public void setAdapter(WheelAdapter<T> adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            adapter.setPicker(this, selectedAreaHeight, maxShowSize);
        }
        super.setAdapter(adapter);
        refreshScrollTranslate();
    }

    @Override
    public boolean isAttachedToWindow() {
        boolean b = super.isAttachedToWindow();
        refreshScrollTranslate();
        return b;
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        refreshScrollTranslate();
    }
//
//        @Override
//        protected void onAttachedToWindow() {
//            super.onAttachedToWindow();
////            mWidth=ViewUtils.calculateViewSize(this)[0];
////            mWidth=getMeasuredWidth();
//            if (adapter != null) adapter.notifyDataSetChanged();
//        }


    private LinearLayoutManager linearLayoutManager;
    /**
     * 中间选择区域的高度,同时也是item高度
     */
    private int selectedAreaHeight = 120;
    /**
     * 设置最大显示数量
     */
    private int maxShowSize = 5;

    /**
     * 设置可见区域最多显示的元素个数，不足奇数将自动转换为奇数
     *
     * @param maxShowSize
     */
    public void setMaxShowSize(int maxShowSize) {
        if (maxShowSize < 1) {
            maxShowSize = 1;
        }
        if (maxShowSize % 2 == 0) {
            maxShowSize += 1;
        }
        this.maxShowSize = maxShowSize;
        if (adapter != null) {
            adapter.setPicker(this, selectedAreaHeight, maxShowSize);
            adapter.notifyDataSetChanged();
        }
        if (getLayoutParams() == null) {
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxShowSize * selectedAreaHeight));
        } else {
            getLayoutParams().height = maxShowSize * selectedAreaHeight;
        }
    }

    /**
     * 默认的
     */
    public AreaDrawer DEFAULT_SELECTED_AREA_DRAWER = new AreaDrawer() {
        @Override
        public void onDraw(Context context, Canvas canvas, Rect rect) {
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(DensityUtil.dip2px(context, 1));
                paint.setColor(Color.parseColor("#cdced3"));
            }
            canvas.drawLine(0, rect.top, rect.right, rect.top, paint);
            canvas.drawLine(0, rect.bottom, rect.right, rect.bottom, paint);
        }

        Paint paint;
    };

    private AreaDrawer selectedAreaDrawer = DEFAULT_SELECTED_AREA_DRAWER;
    private AreaDrawer selectedTopAreaDrawer = null;
    private AreaDrawer selectedBottomAreaDrawer = null;

    /**
     * 设置选中区域的蒙层，比如上下各有一个灰色的分割线
     *
     * @param selectedAreaDrawer
     */
    public void setSelectedAreaDrawer(AreaDrawer selectedAreaDrawer) {
        this.selectedAreaDrawer = selectedAreaDrawer;
    }

    private final void init() {
        setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
//                if (dy==0) {
//                    selectedPosition = getChildViewHolder(findChildViewUnder(getMeasuredWidth() / 2, getMeasuredHeight() / 2)).getAdapterPosition();
//                    Log.e("scrollVerticallyBy", "scroll:" + dy + "***" +
//                            selectedPosition);
//                }
                refreshScrollTranslate();
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        });
    }

    /**
     * 刷新每个元素的UI，控制滚动动画以及缩放效果
     *
     * @param v
     */
    protected final void refreshItemTranslate(View v) {
        v.getLayoutParams().width = mWidth;
        int h = v.getTop() + selectedAreaHeight / 2;//选中view的中线
        int hd = selectedAreaHeight * maxShowSize / 2;//选中区域的中线
        float progress = Math.abs((hd - h) / Float.valueOf(hd + selectedAreaHeight / 2));//需要滚动的距离
        if (progress > 0) {
            progress = 1 - progress;
        } else {
            progress = 1 + progress;
            progress *= -1;
        }
        ViewHolder viewHolder = getChildViewHolder(v);
        if (viewHolder == null) return;
        if (viewHolder.getItemViewType() == WheelAdapter.VIEW_TYPE_HEADER || viewHolder.getItemViewType() == WheelAdapter.VIEW_TYPE_FOOTER) {
            return;
        }
        adapter.onWheelScrollTranslate(viewHolder, progress);
    }

    /**
     * 刷新滚动时的缩放效果
     */
    private void refreshScrollTranslate() {
        for (int i = 0; i < getChildCount(); i++) {
            refreshItemTranslate(getChildAt(i));
        }
    }

    protected int mWidth;
    private int mHeight;

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (mWidth == 0)
            mWidth = MeasureSpec.getSize(widthSpec);
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(mHeight = selectedAreaHeight * maxShowSize, MeasureSpec.getMode(heightSpec)));
    }

    private int selectedPosition = 0;

    private ValueAnimator valueAnimator;

    public void setDefaultValue(T t) {
        int index = adapter.getPositionByValue(t);
        if (index < 0) {
            return;
        }
        linearLayoutManager.scrollToPositionWithOffset(index, 0);
        selectedPosition = index + adapter.itemHeadOrFootSize;
        refreshScrollTranslate();
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (state == 0) {
            View v = findChildViewUnder(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
            if (v != null) {
                selectedPosition = getChildViewHolder(v).getAdapterPosition();
                int d = (selectedPosition - adapter.itemHeadOrFootSize) * selectedAreaHeight - computeVerticalScrollOffset();
                if (d != 0) {
                    valueAnimator = ValueAnimator.ofInt(d, 0);
                    valueAnimator.setDuration(100);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        int i = selectedPosition - adapter.itemHeadOrFootSize;

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            linearLayoutManager.scrollToPositionWithOffset(i, (int) animation.getAnimatedValue());
//                        Log.e("onScrollStateChanged", "d2=" + (int) animation.getAnimatedValue());
                        }
                    });
                    valueAnimator.start();
//                Log.e("onScrollStateChanged", "d=" + d);
                }
                if (adapter != null) {
                    int getSelectedIndex = getSelectedIndex();
                    if (selectedPosition >= 0 && getSelectedIndex >= 0)
                        adapter.onWheelSelected(getChildViewHolder(linearLayoutManager.findViewByPosition(selectedPosition)), getSelectedIndex, getSelected());
                }
            }
        } else {
        }
        super.onScrollStateChanged(state);
    }

    public int getSelectedIndex() {
        return adapter.getDataPosition(selectedPosition);
    }

    public T getSelected() {
        return adapter.getWheelItemData(getSelectedIndex());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Rect top = new Rect(0, 0,
                mWidth, mHeight / 2 - selectedAreaHeight / 2);
        Rect center = new Rect(0, top.bottom,
                mWidth, mHeight / 2 + selectedAreaHeight / 2);
        Rect bottom = new Rect(0, center.bottom,
                mWidth, mHeight);
        if (selectedTopAreaDrawer != null) {
            selectedTopAreaDrawer.onDraw(getContext(), canvas, top);
        }
        if (selectedAreaDrawer != null) {
            selectedAreaDrawer.onDraw(getContext(), canvas, center);
        }

        if (selectedBottomAreaDrawer != null) {
            selectedBottomAreaDrawer.onDraw(getContext(), canvas, bottom);
        }
    }

    @Override
    public void scrollToPosition(int position) {
//        super.scrollToPosition(position);
        linearLayoutManager.scrollToPositionWithOffset(position, 0);
    }

}