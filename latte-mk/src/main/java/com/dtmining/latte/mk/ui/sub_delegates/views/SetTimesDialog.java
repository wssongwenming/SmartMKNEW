package com.dtmining.latte.mk.ui.sub_delegates.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.adapter.MedicineSimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shikun on 18-6-17.
 */

public class SetTimesDialog extends Dialog  implements View.OnClickListener,MedicineSimpleAdapter.Callback{
    private Context context;
    private String confirmButtonText;
    private String cacelButtonText;
    private ClickListenerInterface clickListenerInterface;
    private CustomDatePicker customDatePicker;
    private ListView lv_set_time;
    private ArrayList<String> time_list;
    private  MedicineSimpleAdapter adapter;
    private ClickListenerInterface mclicklistenerinterface;



    @Override
    public void click(final View v) {
        int i = v.getId();
        if (i == R.id.btn_delete) {
            time_list.remove(time_list.get((int) v.getTag()));
            System.out.print(time_list);
            adapter.notifyDataSetChanged();

        }


    }


    public interface ClickListenerInterface {

        public void doConfirm(ArrayList<String> times);

        public void doCancel();
    }

    public SetTimesDialog(Context context, String confirmButtonText, String cacelButtonText, ClickListenerInterface clicklistenerinterface) {
        super(context, R.style.Theme_MYDialog);
        this.context = context;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
        this.mclicklistenerinterface=clicklistenerinterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_edit_time_dialog, null);
        setContentView(view);

        lv_set_time= (ListView) view.findViewById(R.id.lv_set_time);
        time_list=new ArrayList<String>();
        time_list.add("08:00");
        time_list.add("12:00");
        time_list.add("19:00");
        adapter=new MedicineSimpleAdapter(time_list,this.context,"1",SetTimesDialog.this);
        lv_set_time.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker = new CustomDatePicker(this.context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                System.out.println(time);
                time_list.add(time);
                adapter.notifyDataSetChanged();
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(true); // 显示时和分
        customDatePicker.setIsLoop(true); // 允许循环滚动


        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
        Button btnAdd= (Button) view.findViewById(R.id.btn_add_time);


        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cacelButtonText);

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());
        btnAdd.setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_add_time) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            String now = sdf.format(new Date());
            customDatePicker.show(now);

        }

    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.confirm) {
                mclicklistenerinterface.doConfirm(time_list);

            } else if (id == R.id.cancel) {
                mclicklistenerinterface.doCancel();

            }
        }

    };
}