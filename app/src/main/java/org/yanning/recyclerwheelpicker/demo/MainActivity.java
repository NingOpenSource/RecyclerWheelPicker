package org.yanning.recyclerwheelpicker.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.yanning.recyclerwheelpicker.RecyclerWheelPicker;
import org.yanning.recyclerwheelpicker.TextViewWheelAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    private String selected1 = "text_0";
    Toast toast;

    public void textPicker(View v) {
        RecyclerWheelPicker<String> picker = new RecyclerWheelPicker(this);
        picker.setMaxShowSize(7);picker.setOrientation(RecyclerWheelPicker.HORIZONTAL);
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
        new AlertDialog.Builder(this)
                .setView(picker).create().show();
    }

    public void dateTimePicker(View v) {
        View root = getLayoutInflater().inflate(R.layout.dialog_0, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(root).create();
        RecyclerWheelPicker<Long> picker0 = root.findViewById(R.id.list0);
        RecyclerWheelPicker<Long> picker1 = root.findViewById(R.id.list1);
        final SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        final TextView date = root.findViewById(R.id.date);
        final TextView time = root.findViewById(R.id.time);
        try {
            picker0.setAdapter(new TextViewWheelAdapter<Long>() {
                long start = format0.parse("2000-01-01").getTime();
                long end = format0.parse("2222-01-01").getTime();
                long day = 1000 * 60 * 60 * 24;

                @Override
                protected String getWheelItemName(int position, Long aLong) {
                    return format0.format(new Date(aLong));
                }

                @Override
                protected int getPositionByValue(Long aLong) {
                    return (int) ((aLong - start) / day);
                }

                @Override
                protected void onWheelSelected(RecyclerView.ViewHolder holder, int position, Long aLong) {
                    date.setText(format0.format(new Date(aLong)));
                }

                @Override
                protected Long getWheelItemData(int position) {
                    return start + position * day;
                }

                @Override
                protected int getWheelItemCount() {
                    return (int) ((end - start) / day);
                }
            });
            picker1.setAdapter(new TextViewWheelAdapter<Long>() {
                long start = format1.parse("00:00:00").getTime();
                long end = format1.parse("23:59:59").getTime();
                long min = 1000 * 60;

                @Override
                protected String getWheelItemName(int position, Long aLong) {
                    return format1.format(new Date(aLong));
                }

                @Override
                protected int getPositionByValue(Long aLong) {
                    return (int) ((aLong - start) / min);
                }

                @Override
                protected void onWheelSelected(RecyclerView.ViewHolder holder, int position, Long aLong) {
                    time.setText(format1.format(new Date(aLong)));
                }

                @Override
                protected Long getWheelItemData(int position) {
                    return start + position * min;
                }

                @Override
                protected int getWheelItemCount() {
                    return (int) ((end - start) / min);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dialog.show();
    }


    public void imagePicker(View v) {
        toast.setText("可以实现的，不想写了，太懒了~_~");
        toast.show();
    }


    public void emojiPicker(View v) {
        toast.setText("可以实现的，不想写了，太懒了~_~");
        toast.show();
    }

    public void multiLayoutPicker(View v) {
        toast.setText("可以实现的，不想写了，太懒了~_~");
        toast.show();
    }

}
