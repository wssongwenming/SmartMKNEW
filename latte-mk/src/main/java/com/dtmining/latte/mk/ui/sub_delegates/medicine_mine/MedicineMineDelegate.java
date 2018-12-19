package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.mymedicineboxes.MedicineBoxesMineDataConverter;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.index.IndexDelegate;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicinePlanExpandableListViewAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineMineDelegate extends LatteDelegate implements View.OnClickListener,AdapterView.OnItemClickListener,MedicineMineEditDelegate.RefreshListener{
    private String tel=null;
    private MedicineMineRecyclerAdapter medicineMineRecyclerAdapter;
    List<MultipleItemEntity> mDatas=new ArrayList<>();
 /* @BindView(R2.id.srl_medicine_mine)
    SwipeRefreshLayout mRefreshLayout=null;*/
    @BindView(R2.id.rv_medicine_mine)
    RecyclerView mRecyclerView=null;
    @OnClick(R2.id.tb_medicine_mine)
    void searchMedicine(){
        String medicineName=mEditText_Medicine_Name.getText().toString();
        if(!(medicineName.isEmpty()||medicineName==null))
        {
            getMedicineMine(UploadConfig.API_HOST+"/api/get_medicine_by_prename",tel,medicineName);
        }
    }
   @BindView(R2.id.et_search_medicine)
    AppCompatEditText mEditText_Medicine_Name=null;
    @OnClick(R2.id.btn_scan_medicine)
    void searchMedicineByscan(){

    }
    @BindView(R2.id.tb_medicine_mine)
    Toolbar mToolbar=null;
    private RefreshHandler mRefreshHandler=null;
    private static final String BOX_ID = "BOX_ID";
    private String mBoxId ="";
    private Set<SwipeListLayout> sets = new HashSet();
/*    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light

        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }*/


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mBoxId = args.getString(BOX_ID);
        }
    }
    public static MedicineMineDelegate newInstance(String boxId){
        final Bundle args = new Bundle();
        args.putString(BOX_ID,boxId);
        final MedicineMineDelegate delegate = new MedicineMineDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        //initRefreshLayout();
        initRecyclerView();
        //getMedicineMine(UploadConfig.API_HOST+"/api/get_medicine");
        //mRefreshHandler.firstPage_medicine_mine(UploadConfig.API_HOST+"/api/get_medicine",tel,MedicineMineDelegate.this);
        getMedicineMineAll(UploadConfig.API_HOST+"/api/get_medicine",tel);
    }
    private void getMedicineMine(String url,String tel,String medicineName){
         RestClient.builder()
                .url(url)
                .params("tel",tel)
                .params("word",medicineName)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        if(response!=null) {
                            JSONObject object= JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            if(code==1) {
                                Log.d("search", response);
                                mDatas.clear();
                                convert_response_to_medicine_mine(response);
                                //medicineMineRecyclerAdapter=new MedicineMineRecyclerAdapter(mDatas,sets);
                                medicineMineRecyclerAdapter=new MedicineMineRecyclerAdapter(mDatas,sets,MedicineMineDelegate.this);
                                mRecyclerView.setAdapter(medicineMineRecyclerAdapter);
                            }else if(code==17)
                            {
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY),"当前用户没有药品",Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                })
                .build()
                .get();

    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_mine;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRefreshHandler=RefreshHandler.create(null,mRecyclerView,null,new MedincineMineDataConverter(),null,sets);
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }

    }
    private void initRecyclerView(){
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new MyDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        medicineMineRecyclerAdapter = new MedicineMineRecyclerAdapter( mDatas,sets,MedicineMineDelegate.this );
        mRecyclerView.setAdapter(medicineMineRecyclerAdapter);
    }

    public void convert_response_to_medicine_mine(String reponseJsonString) {
        if(reponseJsonString!=null) {

            final JSONObject jsonObject = JSON.parseObject(reponseJsonString);
            String tel = jsonObject.getString("tel");
            int code=jsonObject.getIntValue("code");
            System.out.print("code="+code);
            final JSONArray dataArray = jsonObject.getJSONArray("detail");

            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject data = (JSONObject) dataArray.get(i);
                final String endRemind=data.getString("end_remind");
                final String medicineCode=data.getString("medicine_code");
                final String medicineValidity=data.getString("medicine_validity");
                final String medicineId=data.getString("medicine_id");
                final int medicineType=data.getIntValue("medicine_type");
                final int medicineCount = data.getInteger("medicine_count");
                final int medicineUseCount=data.getIntValue("medicine_useCount");
                final int timesonday=data.getIntValue("timesOnDay");
                final int dayInterval=data.getIntValue("day_interval");
                final String startRemind=data.getString("start_remind");
                final String medicineName = data.getString("medicine_name");
                final String medicine_img_url = data.getString("medicine_url");
                final String boxId = data.getString("box_id");
                final int medicinePause = data.getInteger("medicine_pause");

                int type = ItemType.MEDICINE_MINE;
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setField(MultipleFields.TEL, tel)
                        .setField(MultipleFields.MEDICINEENDREMIND,endRemind)
                        .setField(MultipleFields.MEDICINECODE,medicineCode)
                        .setField(MultipleFields.MEDICINEVALIDITY,medicineValidity)
                        .setField(MultipleFields.MEDICINEID, medicineId)
                        .setField(MultipleFields.MEDICINETYPE,medicineType)
                        .setField(MultipleFields.MEDICINECOUNT, medicineCount)
                        .setField(MultipleFields.MEDICINETIMESONDAY,timesonday)
                        .setField(MultipleFields.MEDICINEINTERVAL,dayInterval)
                        .setField(MultipleFields.MEDICINESTARTREMIND,startRemind)
                        .setField(MultipleFields.MEDICINENAME, medicineName)
                        .setField(MultipleFields.MEDICINEIMGURL, medicine_img_url)
                        .setField(MultipleFields.MEDICINEUSECOUNT,medicineUseCount)
                        .setField(MultipleFields.BOXID, boxId)
                        .setField(MultipleFields.MEDICINEPAUSE, medicinePause)
                        .build();
                mDatas.add(entity);

            }
        }
        //return mDatas;
    }
    public List<MultipleItemEntity> convert(String reponseJsonString) {
        if(reponseJsonString!=null) {
            mDatas.clear();
            final JSONObject jsonObject = JSON.parseObject(reponseJsonString);
            String tel = jsonObject.getString("tel");
            int code=jsonObject.getIntValue("code");
            System.out.print("code="+code);
            final JSONArray dataArray = jsonObject.getJSONArray("detail");

            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject data = (JSONObject) dataArray.get(i);
                final String endRemind=data.getString("endRemind");
                final String medicineCode=data.getString("medicineCode");
                final String medicineValidity=data.getString("medicineValidity");
                final String medicineId=data.getString("medicineId");
                final int medicineCount = data.getInteger("medicineCount");
                final int medicineType=data.getIntValue("medicineType");
                final int medicineUseCount=data.getIntValue("medicineUsecount");
                final int timesonday=data.getIntValue("timesonday");
                final int dayInterval=data.getIntValue("dayInterval");
                final String startRemind=data.getString("startRemind");
                final String medicineName = data.getString("medicineName");
                final String medicine_img_url = data.getString("medicineUrl");
                final String boxId = data.getString("boxId");
                final int medicinePause = data.getInteger("medicinePause");
                int type = ItemType.MEDICINE_MINE;
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setField(MultipleFields.TEL, tel)
                        .setField(MultipleFields.MEDICINEENDREMIND,endRemind)
                        .setField(MultipleFields.MEDICINECODE,medicineCode)
                        .setField(MultipleFields.MEDICINEVALIDITY,medicineValidity)
                        .setField(MultipleFields.MEDICINEID, medicineId)
                        .setField(MultipleFields.MEDICINETYPE,medicineType)
                        .setField(MultipleFields.MEDICINECOUNT, medicineCount)
                        .setField(MultipleFields.MEDICINETIMESONDAY,timesonday)
                        .setField(MultipleFields.MEDICINEINTERVAL,dayInterval)
                        .setField(MultipleFields.MEDICINESTARTREMIND,startRemind)
                        .setField(MultipleFields.MEDICINENAME, medicineName)
                        .setField(MultipleFields.MEDICINEIMGURL, medicine_img_url)
                        .setField(MultipleFields.MEDICINEUSECOUNT,medicineUseCount)
                        .setField(MultipleFields.BOXID, boxId)
                        .setField(MultipleFields.MEDICINEPAUSE, medicinePause)
                        .build();
                mDatas.add(entity);
            }
        }
        return mDatas;
    }
    private void getMedicineMineAll(String url,String tel){
        RestClient.builder()
                .url(url)
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null) {
                            JSONObject object= JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            if(code==1) {
                                Log.d("search", response);
                                mDatas.clear();
                                convert(response);
                                //medicineMineRecyclerAdapter=new MedicineMineRecyclerAdapter(mDatas,sets);
                                //medicineMineRecyclerAdapter=new MedicineMineRecyclerAdapter(mDatas,sets,MedicineMineDelegate.this);
                                //mRecyclerView.setAdapter(medicineMineRecyclerAdapter);
                                medicineMineRecyclerAdapter.notifyDataSetChanged();
                            }else if(code==17)
                            {
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY),"当前用户没有药品",Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                })
                .build()
                .get();

    }
    @Override
    public void onRefresh() {
        getMedicineMineAll(UploadConfig.API_HOST+"/api/get_medicine",tel);

    }
}
