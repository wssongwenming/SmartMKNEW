package com.dtmining.latte.mk.main.manage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.adapter.HorizontalAdapter;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicine.AddMedicineDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.MedicineOverdueDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_summary.MedicineSummaryDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicineTakePlanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.HorizontalListview;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/10/10
 * Description:
 */
public class ManageDelegate extends BottomItemDelegate {
    private String boxId=null;
    private String tel=null;
    @BindView(R2.id.hl_recently_useed_medicine)
    HorizontalListview horizontalListview=null;
    @OnClick(R2.id.manage_medicine_add)
    void addMedicine(){
        getParentDelegate().start(new AddMedicineDelegate());
    }
    @OnClick(R2.id.manage_medicine_list)
    void medicineList(){
        getParentDelegate().start(new MedicineMineDelegate());
    }
    @OnClick(R2.id.manage_medicine_plan)
    void medicinePlan(){
        getParentDelegate().start(new MedicineTakePlanDelegate());
    }
    @OnClick(R2.id.manage_medicine_overtime)
    void medicineOverTime(){
        getParentDelegate().start(new MedicineOverdueDelegate());
    }
    @OnClick(R2.id.manage_medicine_summary)
    void medicineSummary(){
        getParentDelegate().start(new MedicineSummaryDelegate());
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_manage;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId= LattePreference.getBoxId();
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            if(boxId==null){
                Toast.makeText(getContext(),"App未绑定当前药箱",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_recently_use")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        com.alibaba.fastjson.JSONObject object=JSON.parseObject(response);
                        int code=object.getIntValue("code");

                        //ManagerDataConverter managerDataConverter=new ManagerDataConverter(response);
                        //HorizontalAdapter horizontalAdapter = new HorizontalAdapter(managerDataConverter.convert(), getContext());
                        //horizontalListview.setAdapter(horizontalAdapter);
                    }
                })
                .build()
                .get();
    }

}
