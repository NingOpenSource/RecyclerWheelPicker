import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecyclerWheelPicker<T> extends RecyclerView {
    public RecyclerWheelPicker(Context context) {
        super(context);
        init();
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private WheelAdapter<T> adapter;

    public void setSelectedTopAreaDrawer(AreaDrawer selectedTopAreaDrawer) {
        this.selectedTopAreaDrawer = selectedTopAreaDrawer;
    }

    public void setSelectedBottomAreaDrawer(AreaDrawer selectedBottomAreaDrawer) {
        this.selectedBottomAreaDrawer = selectedBottomAreaDrawer;
    }

    public void setSelectedAreaHeight(int selectedAreaHeight) {
        this.selectedAreaHeight = selectedAreaHeight;
        if (adapter != null) {
            adapter.setPicker(this, selectedAreaHeight, maxShowSize);
            adapter.notifyDataSetChanged();
        }
    }

    public void onClickItemView(View v){
        ViewHolder viewHolder=getChildViewHolder(v);
        if (viewHolder!=null){
            int h=v.getTop()-adapter.itemHeadOrFootSize*selectedAreaHeight;
            smoothScrollBy(0,h);
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

    private LinearLayoutManager linearLayoutManager;
    /**
     * 中间选择区域的高度,同时也是item高度
     */
    private int selectedAreaHeight = 120;
    /**
     * 设置最大显示数量
     */
    private int maxShowSize = 5;

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
    }

    /**
     * 默认的
     */
    public static AreaDrawer DEFAULT_SELECTED_AREA_DRAWER = new AreaDrawer() {
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

    public void setSelectedAreaDrawer(AreaDrawer selectedAreaDrawer) {
        this.selectedAreaDrawer = selectedAreaDrawer;
    }

    public interface AreaDrawer {
        void onDraw(Context context, Canvas canvas, Rect rect);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    private void init() {
        setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
                Log.e("scrollVerticallyBy", "scroll:" +
                        getChildViewHolder(findChildViewUnder(getMeasuredWidth() / 2, getMeasuredHeight() / 2)).getAdapterPosition());
//                adapter.onScrollProgress2(getChildViewHolder(findViewByPosition()));
                refreshScrollTranslate();
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        });
    }

    private void refreshItemTranslate(View v) {
        int h = v.getTop() + selectedAreaHeight / 2;
        int hd = getMeasuredHeight() / 2;
        float progress = Math.abs((hd - h) / Float.valueOf(hd + selectedAreaHeight / 2));
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
        adapter.onScrollTranslate2(viewHolder, progress);
    }

    /**
     * 刷新滚动时的缩放效果
     */
    private void refreshScrollTranslate() {
        for (int i = 0; i < getChildCount(); i++) {
            refreshItemTranslate(getChildAt(i));
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(selectedAreaHeight * maxShowSize, MeasureSpec.getMode(heightSpec)));
    }

    private T selectedData;
    private int selectedPosition = 0;


    private int calculateSelectedPosition() {
        float r = computeVerticalScrollOffset() / Float.valueOf(selectedAreaHeight);
        selectedPosition = Math.round(r);
        selectedData = adapter.getItemData2(getSelectedIndex());
        return selectedPosition;
    }

    @Override
    public void onScrollStateChanged(int state) {
        calculateSelectedPosition();
        if (state == 0) {
            int d = selectedPosition * selectedAreaHeight - computeVerticalScrollOffset();
            smoothScrollBy(0, d);
            if (adapter != null) {
                adapter.onSelected2(getChildViewHolder(linearLayoutManager.findViewByPosition(selectedPosition)), getSelectedIndex(), getSelected());
            }
        }
        super.onScrollStateChanged(state);
    }

    public int getSelectedIndex() {
        return adapter.getDataPosition(selectedPosition);
    }

    public T getSelected() {
        return adapter.getItemData2(getSelectedIndex());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Rect top = new Rect(0, 0,
                getMeasuredWidth(), getMeasuredHeight() / 2 - selectedAreaHeight / 2);
        Rect center = new Rect(0, top.bottom,
                getMeasuredWidth(), getMeasuredHeight() / 2 + selectedAreaHeight / 2);
        Rect bottom = new Rect(0, center.bottom,
                getMeasuredWidth(), getMeasuredHeight());
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

    public static class WheelViewHolder extends ViewHolder {

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
    }

    public static abstract class WheelAdapter<T> extends Adapter<WheelViewHolder> {
        private static final int VIEW_TYPE_HEADER = -234234;
        private static final int VIEW_TYPE_FOOTER = -23674632;
        private int itemHeight = 120;
        private int itemHeadOrFootSize = 2;
        private RecyclerWheelPicker<T> picker;

        /**
         * @param itemHeight
         * @param itemMaxShowSize 必须为奇数
         */
        private void setPicker(RecyclerWheelPicker<T> picker, int itemHeight, int itemMaxShowSize) {
            this.itemHeight = itemHeight;
            this.itemHeadOrFootSize = (itemMaxShowSize - 1) / 2;
            this.picker = picker;
        }

        /**
         * 获取真实的position
         *
         * @param position
         * @return
         */
        private final int getDataPosition(int position) {
            if (position < itemHeadOrFootSize) {
                return 0;
            } else if (position >= getItemCount2()) {
                return getItemCount2() - 1;
            } else {
                return position - itemHeadOrFootSize;
            }
        }

        public RecyclerWheelPicker<T> getPicker() {
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
                return new WheelViewHolder(new View(parent.getContext())).setItemLayoutParameter(itemHeight);
            } else if (viewType == VIEW_TYPE_FOOTER) {
                return new WheelViewHolder(new View(parent.getContext())).setItemLayoutParameter(itemHeight);
            }
            return onCreateViewHolder2(parent).setItemLayoutParameter(itemHeight);
        }

        /**
         * @param parent
         * @return
         */
        abstract WheelViewHolder onCreateViewHolder2(@NonNull ViewGroup parent);

        /**
         * @param holder
         * @param position
         * @param t
         */
        abstract void onSelected2(ViewHolder holder, int position, T t);

        /**
         * item形变
         *
         * @param holder
         * @param progress 0~1
         */
        abstract void onScrollTranslate2(ViewHolder holder, float progress);

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
            return getItemViewType2(position - 1);
        }

        public int getItemViewType2(int position) {
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
            if (getItemViewType(position) != VIEW_TYPE_FOOTER && getItemViewType(position) != VIEW_TYPE_HEADER) {
                int vP = getDataPosition(position);
                onBindViewHolder2(viewHolder, vP, getItemData2(vP));
                picker.refreshItemTranslate(viewHolder.itemView);
            }
        }


        abstract void onBindViewHolder2(@NonNull WheelViewHolder holder, int position, T t);

        /**
         * {@hide}
         *
         * @return
         */
        @Override
        public final int getItemCount() {
            return getItemCount2() + 1;
        }

        abstract T getItemData2(int position);

        abstract int getItemCount2();
    }

    @Override
    public void scrollToPosition(int position) {
//        super.scrollToPosition(position);
        linearLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    public static abstract class TextViewWheelAdapter<T> extends WheelAdapter<T> {
        @Override
        WheelViewHolder onCreateViewHolder2(@NonNull ViewGroup parent) {
            return new RecyclerWheelPicker.WheelViewHolder(new TextView(parent.getContext())) {
                {
                    TextView textView = (TextView) itemView;
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextColor(Color.argb(255, 90, 90, 90));
                    textView.setPadding(20, 20, 20, 20);
                }
            };
        }

        @Override
        void onBindViewHolder2(@NonNull WheelViewHolder holder, int position, T t) {
            ((TextView) holder.itemView).setText(getItemName(position, t));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPicker().onClickItemView(v);
                }
            });
        }

        @Override
        void onScrollTranslate2(ViewHolder holder, float progress) {
            TextView textView = ((TextView) holder.itemView);
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
        abstract String getItemName(int position, T t);
    }
}
