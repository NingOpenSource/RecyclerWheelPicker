package org.yanning.recyclerwheelpicker.demo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import org.yanning.recyclerwheelpicker.RecyclerWheelPicker;
import org.yanning.recyclerwheelpicker.TextViewWheelAdapter;
import org.yanning.recyclerwheelpicker.extra.LinearLayoutX;

@SuppressLint("LongLogTag")
public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG="MyBottomSheetDialogFragment";
    // 构造方法
    public static MyBottomSheetDialogFragment newInstance(Long feedId) {
        Bundle args = new Bundle();
        args.putLong("FEED_ID", feedId);
        MyBottomSheetDialogFragment fragment = new MyBottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // show的时候调用
    @Override
    public void show(FragmentManager manager, String tag) {
        Log.e(TAG, "show");
        super.show(manager, tag);
    }
    // create dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.e(TAG, "onCreateDialog");
        return super.onCreateDialog(savedInstanceState);
    }
    // 创建View
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");RecyclerWheelPicker<String> picker0 = createTextPickerSimple();
        RecyclerWheelPicker<String> picker1 = createTextPickerSimple();
        LinearLayout rootLayout = new LinearLayout(getContext());
        LinearLayoutX linearLayout = new LinearLayoutX(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(picker0, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT) {
            {
                this.weight = 1;
            }
        });
        linearLayout.addView(picker1, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT) {
            {
                this.weight = 1;
            }
        });
        rootLayout.addView(new AppCompatTextView(getContext()) {
                               {
                                   setText("文本选择器(BottomSheetDialogFragment)");
                                   setGravity(Gravity.CENTER);
                                   setPadding(20,20,20,20);
                               }
                           }, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        rootLayout.addView(linearLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        return rootLayout;
    }


    private String selected1 = "text_0";

    private RecyclerWheelPicker<String> createTextPickerSimple() {
        RecyclerWheelPicker<String> picker = new RecyclerWheelPicker(getContext());
        picker.setMaxShowSize(7);
        picker.setOrientation(RecyclerWheelPicker.HORIZONTAL);
        picker.setSelectedAreaHeight(100);
        picker.setAdapter(new TextViewWheelAdapter<String>() {
            @Override
            protected String getWheelItemName(int position, String s) {
                return s;
            }

            @Override
            protected int getPositionByValue(String s) {
                return Integer.valueOf(s.replace("text_", ""));
            }

            @Override
            protected void onWheelSelected(RecyclerView.ViewHolder holder, int position, String s) {
                selected1 = s;
            }

            @Override
            protected String getWheelItemData(int position) {
                return "text_" + position;
            }

            @Override
            protected int getWheelItemCount() {
                return 10000;
            }
        });
        picker.setDefaultValue(selected1);
        return picker;
    }

}
