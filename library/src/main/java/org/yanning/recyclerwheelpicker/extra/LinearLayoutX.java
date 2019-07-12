package org.yanning.recyclerwheelpicker.extra;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild;

public class LinearLayoutX extends LinearLayout implements NestedScrollingChild {
    public LinearLayoutX(Context context) {
        super(context);
        init();
    }

    public LinearLayoutX(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearLayoutX(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinearLayoutX(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setNestedScrollingEnabled(true);
    }

}
