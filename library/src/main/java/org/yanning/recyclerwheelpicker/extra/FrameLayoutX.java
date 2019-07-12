package org.yanning.recyclerwheelpicker.extra;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.NestedScrollingChild;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class FrameLayoutX extends FrameLayout implements NestedScrollingChild {
    public FrameLayoutX(Context context) {
        super(context);
        init();
    }

    public FrameLayoutX(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameLayoutX(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FrameLayoutX(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setNestedScrollingEnabled(true);
    }

}
