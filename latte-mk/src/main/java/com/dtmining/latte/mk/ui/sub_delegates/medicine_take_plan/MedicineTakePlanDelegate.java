package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineTakePlanDelegate extends LatteDelegate{
    @BindView(R2.id.btn_medicine_take_plan_add)
    AppCompatButton mBtnAdd=null;
    @BindView(R2.id.elv_medicine_take_plan_expandableListView)
    ExpandableListView mExpandableListView=null;
    @OnClick(R2.id.btn_medicine_take_plan_add)
    void onClickAddPlan(){
        start(new MedicinePlanAddChoiceDelegate());
    }
    private Set<SwipeListLayout> sets = new HashSet();
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            String tel=Long.toString(userProfile.getTel());

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ok", "onResume: ");
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        RestClient.builder()
                .url("medicine_plan")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        MedicinePlanExpandableListViewAdapter medicinePlanExpandableListViewAdapter=new MedicinePlanExpandableListViewAdapter(response,sets);
                        MedicinePlanExpandableListViewAdapter.convert(response);
                        mExpandableListView.setAdapter(medicinePlanExpandableListViewAdapter);
                    }
                })
                .build()
                .post();
    }
}
