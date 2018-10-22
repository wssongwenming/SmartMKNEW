package com.dtmining.latte.mk.ui.sub_delegates.hand_add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dtmining.latte.R;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class HandAddDelegate extends LatteDelegate{
    private EditText name;
    private Button PD;
    private Button overdue;
    private Spinner spinner;
    private EditText count;
    private Button medicineaddconform;

    private Context context;
    private String usertel;


    private LinkedList<String> myboxes;
    private String choose_box;
    private boolean selected=false;

    private Date date = null;
    private String begin_time;
    private String over_time;
    private int _year;
    private int _month;
    private int _day;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_hand_add;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile!=null){




        }

    }



}
