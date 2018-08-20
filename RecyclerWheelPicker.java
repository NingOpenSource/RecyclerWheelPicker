
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <a href="https://github.com/NingOpenSource/RecyclerWheelPicker">https://github.com/NingOpenSource/RecyclerWheelPicker</a>
 * create bu yanning
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

    private final void setCoreViewLayoutParams(ViewGroup.LayoutParams params) {
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(params);
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

    public void setDefaultValue(T t) {
        coreView.setDefaultValue(t);
    }

    public int getSelectedIndex() {
        return coreView.getSelectedIndex();
    }

    public T getSelected() {
        return coreView.getSelected();
    }

    public void scrollToPosition(int position) {
//        super.scrollToPosition(position);
        coreView.scrollToPosition(position);
    }

    private static class RecyclerWheelPickerCoreView<T> extends RecyclerView {
        public RecyclerWheelPickerCoreView(Context context) {
            super(context);
            init();
        }

        private WheelAdapter<T> adapter;

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

        private final void onClickItemView(final View v) {
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

        private final void refreshItemTranslate(View v) {
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

        private int mWidth;
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

    /**
     * 蒙层模板
     */
    public interface AreaDrawer {
        void onDraw(Context context, Canvas canvas, Rect rect);
    }


    public static class WheelViewHolder extends RecyclerView.ViewHolder {

        public WheelViewHolder(View itemView) {
            super(itemView);
        }

        private final WheelViewHolder setItemLayoutParameter(int height) {
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

    public static abstract class WheelAdapter<T> extends RecyclerView.Adapter<WheelViewHolder> {
        private static final int VIEW_TYPE_HEADER = -234234;
        private static final int VIEW_TYPE_FOOTER = -23674632;
        private int itemHeight = 120;
        private int itemHeadOrFootSize = 2;
        private RecyclerWheelPickerCoreView<T> picker;

        protected int getItemHeight() {
            return itemHeight;
        }

        protected abstract int getPositionByValue(T t);

        /**
         * @param itemHeight
         * @param itemMaxShowSize 必须为奇数
         */
        private void setPicker(RecyclerWheelPickerCoreView<T> picker, int itemHeight, int itemMaxShowSize) {
            this.itemHeight = itemHeight;
            this.itemHeadOrFootSize = (itemMaxShowSize - 1) / 2;
            this.picker = picker;
        }

        /**
         * 获取数据的真实position
         *
         * @param position
         * @return
         */
        private final int getDataPosition(int position) {
            if (position < itemHeadOrFootSize) {
                return 0;
            } else if (position >= getWheelItemCount() + itemHeadOrFootSize) {
                return getWheelItemCount() - 1;
            } else {
                return position - itemHeadOrFootSize;
            }
        }

        public RecyclerWheelPickerCoreView<T> getPicker() {
            return picker;
        }

        /**
         * {@hide}
         *
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public final WheelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_HEADER) {
                return new WheelViewHolder(new LinearLayout(parent.getContext())).setItemLayoutParameter(itemHeight);
            } else if (viewType == VIEW_TYPE_FOOTER) {
                return new WheelViewHolder(new LinearLayout(parent.getContext())).setItemLayoutParameter(itemHeight);
            }
            return onWheelCreateViewHolder(parent).setItemLayoutParameter(itemHeight);
        }

        /**
         * @param parent
         * @return
         */
        protected abstract WheelViewHolder onWheelCreateViewHolder(@NonNull ViewGroup parent);

        /**
         * @param holder
         * @param position
         * @param t
         */
        protected abstract void onWheelSelected(RecyclerView.ViewHolder holder, int position, T t);

        /**
         * item形变
         *
         * @param holder
         * @param progress 0~1
         */
        protected abstract void onWheelScrollTranslate(RecyclerView.ViewHolder holder, float progress);

        /**
         * {@hide}
         *
         * @param position
         * @return
         */
        @Override
        public final int getItemViewType(int position) {
            if (position < itemHeadOrFootSize) {
                return VIEW_TYPE_HEADER;
            }
            if (position > getItemCount() - 1 - itemHeadOrFootSize) {
                return VIEW_TYPE_FOOTER;
            }
            return getWheelItemViewType(position - 1);
        }

        public int getWheelItemViewType(int position) {
            return 0;
        }

        /**
         * {@hide}
         *
         * @param viewHolder
         * @param position
         */
        @Override
        public final void onBindViewHolder(@NonNull WheelViewHolder viewHolder, int position) {
//            viewHolder.itemView.getLayoutParams().height=itemHeight;
            if (getItemViewType(position) != VIEW_TYPE_FOOTER && getItemViewType(position) != VIEW_TYPE_HEADER) {
                int vP = getDataPosition(position);
                onWheelBindViewHolder(viewHolder, vP, getWheelItemData(vP));
                picker.refreshItemTranslate(viewHolder.itemView);
            }
        }


        protected abstract void onWheelBindViewHolder(@NonNull WheelViewHolder holder, int position, T t);

        /**
         * {@hide}
         *
         * @return
         */
        @Override
        public final int getItemCount() {
            return getWheelItemCount() + itemHeadOrFootSize * 2;
        }

        protected abstract T getWheelItemData(int position);

        protected abstract int getWheelItemCount();
    }

    public static abstract class TextViewWheelAdapter<T> extends WheelAdapter<T> {
        private static final int textId = 0x3467;

        public static int getTextId() {
            return textId;
        }

        @Override
        protected WheelViewHolder onWheelCreateViewHolder(@NonNull ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new RecyclerView.LayoutParams(getPicker().mWidth, getItemHeight()));
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.argb(255, 90, 90, 90));
            textView.setPadding(20, 20, 20, 20);
            textView.setId(textId);
            linearLayout.addView(textView);
            return new RecyclerWheelPicker.WheelViewHolder(linearLayout) {
            };
        }

        @Override
        protected void onWheelBindViewHolder(@NonNull WheelViewHolder holder, int position, T t) {
            TextView textView = holder.itemView.findViewById(textId);
            textView.setText(getWheelItemName(position, t));
            holder.itemView.setOnClickListener(new OnClickListener() {
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
}
