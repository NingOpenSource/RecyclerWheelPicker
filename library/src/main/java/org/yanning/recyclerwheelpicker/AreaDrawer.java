package org.yanning.recyclerwheelpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 蒙层模板
 */
public interface AreaDrawer {
    void onDraw(Context context, Canvas canvas, Rect rect);
}