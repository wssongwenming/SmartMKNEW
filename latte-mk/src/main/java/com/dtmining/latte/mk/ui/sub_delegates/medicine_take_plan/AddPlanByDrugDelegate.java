package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.adapter.SimpleHorizontalAdapter;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.views.HorizontalListview;
import com.dtmining.latte.mk.ui.sub_delegates.views.SetTimesDialog;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/11/2
 * Description:
 */
public class AddPlanByDrugDelegate extends LatteDelegate implements SetTimesDialog.ClickListenerInterface {
    String tel=null;
    String boxId=null;
    SetTimesDialog setTimesDialog=null;
    private MedicineListDataConverter converter=null;
    private MedicineListAdapter mAdapter=null;
    private ArrayList<String> timeSet=new ArrayList<String>();
    @BindView(R2.id.take_plan_add_by_drug_horizontallistview)
    HorizontalListview mHorizontalListView=null;
    @BindView(R2.id.sp_delegate_medicine_take_plan_add_by_drug_medicine_name)
    Spinner mMedicineListSpinner=null;
    @BindView(R2.id.btn_delegate_medicine_take_plan_add_by_drug_time_span)
    Spinner mTimeSpanSpinner=null;
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_time_set)
    void setPlanTime(){
        setTimesDialog = new SetTimesDialog(getContext() , "确定","取消", this);
        setTimesDialog.show();
    }
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_confirm)
    void confirmPlan(){

    }
    @OnItemSelected(R2.id.sp_delegate_medicine_take_plan_add_by_drug_medicine_name)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MedicineModel medicineModel=mAdapter.getItem(position);
        MedicinePlans medicinePlans=medicineModel.getMedicinePlans();
        if(medicinePlans!=null)
        {
            timeSet=medicineModel.getMedicinePlans().getTime();
            set_time_tag(timeSet);

        }else
        {
            set_time_tag(new ArrayList<String>());
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan_add_by_drug;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId=LattePreference.getBoxId("boxId");
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            if(boxId==null){
                Toast.makeText(getContext(),"App未绑定当前药箱",Toast.LENGTH_LONG).show();
            }
        }
        ArrayAdapter adap = new ArrayAdapter<String>(getContext(), R.layout.single_item_tv, new String[]{"每天", "间隔1天","间隔2天","间隔3天","间隔4天","间隔5天","间隔6天","间隔7天","间隔8天","间隔9天"});
        mTimeSpanSpinner.setAdapter(adap);
        getMedicineList();
    }
    private void getMedicineList(){
        RestClient.builder()
                .url("http://10.0.2.2:8081/Web01_exec/get_medicine_of_box")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        converter=new MedicineListDataConverter();
                        mAdapter= MedicineListAdapter.create(converter.setJsonData(response),R.layout.simple_single_item_list);
                        mMedicineListSpinner.setAdapter(mAdapter);
                    }
                })
                .build()
                .get();

    }
    // 设置时间提示标签　tips
    private void set_time_tag(ArrayList<String> the_time_tags){
        SimpleHorizontalAdapter  horizontalAdapter = new SimpleHorizontalAdapter(the_time_tags, getContext());
        mHorizontalListView.setAdapter(horizontalAdapter);
        //horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void doConfirm(ArrayList<String> times) {
        timeSet=times;
        set_time_tag(timeSet);
        setTimesDialog.dismiss();
    }

    @Override
    public void doCancel() {
        setTimesDialog.dismiss();
    }
}
