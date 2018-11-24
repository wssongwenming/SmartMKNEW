package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/24
 * Description:
 */
public class MedicineReactionDelegate extends LatteDelegate {
    private static final String HISTORY_ID = "MEDICINE_USE_HISTORY_ID";
    private String medicineUseHistoryId;
    private String reaction="优";
    @OnClick(R2.id.btn_medical_reaction_confirm)
    void submitReaction(){
        JsonObject detail=new JsonObject();
        detail.addProperty("id",medicineUseHistoryId);
        detail.addProperty("reaction",reaction);
        JsonObject reactionSubModel=new JsonObject();
        reactionSubModel.add("detail",detail);
        RestClient.builder()
                .url("")
                .clearParams()
                .raw(reactionSubModel.toString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                       pop();
                    }
                })
                .build()
                .post();

    }
    @BindView(R2.id.medicine_reaction_radioGroup)
    RadioGroup radioGroup=null;



    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_reaction;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radbtn = (RadioButton) getView().findViewById(checkedId);
                reaction=radbtn.getText().toString();
            }
        });

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            medicineUseHistoryId = args.getString(HISTORY_ID);
            Toast.makeText(getContext(),medicineUseHistoryId+"ok",Toast.LENGTH_LONG).show();
        }


    }
    public static  MedicineReactionDelegate newInstance(String medicineUseHistoryId){
        final Bundle args = new Bundle();
        args.putString(HISTORY_ID,medicineUseHistoryId);
        final  MedicineReactionDelegate delegate = new  MedicineReactionDelegate();
        delegate.setArguments(args);
        return delegate;
    }

}
