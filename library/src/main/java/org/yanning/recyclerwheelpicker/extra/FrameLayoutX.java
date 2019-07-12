package org.yanning.recyclerwheelpicker.extra;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild;

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
