package com.dtmining.latte.mk.main.aboutme.mymedicineboxes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineEditDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineRecyclerAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedincineMineDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineMineDelegateForBox extends LatteDelegate implements View.OnClickListener,AdapterView.OnItemClickListener,MedicineMineEditDelegate.RefreshListener{
    private String tel=null;
    private MedicineMineRecyclerAdapter medicineMineRecyclerAdapter;
    List<MultipleItemEntity> mDatas=new ArrayList<>();
 /* @BindView(R2.id.srl_medicine_mine)
    SwipeRefreshLayout mRefreshLayout=null;*/
    @BindView(R2.id.rv_medicine_mine)
    RecyclerView mRecyclerView=null;
/*    @BindView(R2.id.ed_search_medicine)
    AppCompatEditText mEditText_Medicine_Name=null;
    @BindView(R2.id.btn_search_medicine)
    AppCompatButton mBtn_Search_Medicine=null;
    @BindView(R2.id.btn_scan_medicine)
    AppCompatButton mBtn_Scan_Medicine=null;
    @BindView(R2.id.tb_medicine_mine)*/
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
    public static MedicineMineDelegateForBox newInstance(String boxId){
        final Bundle args = new Bundle();
        args.putString(BOX_ID,boxId);
        final MedicineMineDelegateForBox delegate = new MedicineMineDelegateForBox();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        //initRefreshLayout();
        initRecyclerView();
        //getMedicineMine(UploadConfig.API_HOST+"/api/get_medicine");
       // mRefreshHandler.firstPage_medicine_mine_with_boxId(UploadConfig.API_HOST+"/api/get_medicine_of_box",tel,mBoxId,MedicineMineDelegateForBox.this);
        getMedicineMineInbox(UploadConfig.API_HOST+"/api/get_medicine_of_box",tel,mBoxId);
    }
    private void getMedicineMine(String url,String tel,String boxId){
        RestClient.builder()
                .url(url)
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        if(response!=null) {
                            JSONObject object= JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            if(code==1) {
                                convert_response_to_medicine_mine(response);
                                //medicineMineRecyclerAdapter=new MedicineMineRecyclerAdapter(mDatas,sets);
                                medicineMineRecyclerAdapter.notifyDataSetChanged();
                                //mRecyclerView.setAdapter(medicineMineRecyclerAdapter);
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
/*      RestClient.builder()
                .url("medicine_mine")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final MedincineMineDataConverter converter=new MedincineMineDataConverter();
                        converter.setJsonData(response);
                        ArrayList<MultipleItemEntity> list=converter.convert();
                        list.get(0).getField("medicineName");
                        Toast.makeText(getContext(),list.get(0).getField(MedicineMineFields.MEDICINENAME).toString(),Toast.LENGTH_LONG).show();
                    }
                })
                .build()
                .get();*/
    }
    private void initRecyclerView(){
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new MyDecoration(getContext(), DividerItemDecoration.VERTICAL));
/*        mRecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    case SCROLL_STATE_IDLE:
                        if (sets.size() > 0) {
                            for (SwipeListLayout s : sets) {
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;

                }
            }
        });*/
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
        medicineMineRecyclerAdapter = new MedicineMineRecyclerAdapter( mDatas,sets,MedicineMineDelegateForBox.this);
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
                final String endRemind=data.getString("endRemind");
                final String medicineCode=data.getString("medicineCode");
                final String medicineValidity=data.getString("medicineValidity");
                final String medicineId=data.getString("medicineId");
                final int medicineCount = data.getInteger("medicineCount");
                final String medicineName = data.getString("medicineName");
                final String medicine_img_url = data.getString("medicineUrl");
                final String boxId = data.getString("boxId");
                final int medicinePause = data.getInteger("medicinePause");
                int type = ItemType.MEDICINE_MINE;
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setField(MultipleFields.TEL, tel)
                        .setField(MultipleFields.MEDICINEID, medicineId)
                        .setField(MultipleFields.MEDICINECOUNT, medicineCount)
                        .setField(MultipleFields.MEDICINENAME, medicineName)
                        .setField(MultipleFields.MEDICINEIMGURL, medicine_img_url)
                        .setField(MultipleFields.BOXID, boxId)
                        .setField(MultipleFields.MEDICINEPAUSE, medicinePause)
                        .build();
                mDatas.add(entity);

            }
        }
        //return mDatas;
    }

    @Override
    public void onRefresh() {
        getMedicineMineInbox(UploadConfig.API_HOST+"/api/get_medicine_of_box",tel,mBoxId);
    }

    private void getMedicineMineInbox(String url,String tel,String boxId){
        RestClient.builder()
                .url(url)
                .params("tel",tel)
                .params("boxId",boxId)
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
}
