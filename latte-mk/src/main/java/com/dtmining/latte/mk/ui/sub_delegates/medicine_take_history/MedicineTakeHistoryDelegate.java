package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.layoutmanager.MyLayoutManager;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.index.IndexDataConverter;
import com.dtmining.latte.mk.main.index.IndexDelegate;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;
import com.dtmining.latte.mk.ui.refresh.PagingBean;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicinePlanExpandableListViewAdapter;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineTakeHistoryDelegate extends LatteDelegate {
    //显示用药历史
    private ArrayList<MultipleItemEntity> medicineHistoryList=new ArrayList<>();
    private MultipleRecyclerAdapter medicineHistoryRecyclerViewAdapter;
    private PagingBean BEAN;

    private RefreshHandler mRefreshHandler=null;
    @BindView(R2.id.srl_medicine_history)
    SwipeRefreshLayout mSwipeRefreshLayout=null;
    @BindView(R2.id.rv_medicine_history)
    RecyclerView mRecyclerViewHistory=null;
    String boxId=null;
    String tel=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_history;
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRefreshHandler=RefreshHandler.create(mSwipeRefreshLayout,mRecyclerViewHistory,
                null,new IndexDataConverter(),this,null);
        medicineHistoryRecyclerViewAdapter= MultipleRecyclerAdapter.create(medicineHistoryList,this);
        mRecyclerViewHistory.setAdapter(medicineHistoryRecyclerViewAdapter);
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            boxId=LattePreference.getBoxId();
            if(boxId.equalsIgnoreCase("未设置boxId"))
            {
                Toast.makeText(getContext(),"请添加药箱，并绑定当前药箱",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);
    }
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerViewHistory.setLayoutManager(linearLayoutManager);
        mRecyclerViewHistory.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initRefreshLayout();
        //mRefreshHandler.get_medicine_history("index",tel,1,20);
        get_medicine_history(UploadConfig.API_HOST+"/api/get_history",tel,0,6);
        //get_medicine_history("index",tel,1,20);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BEAN=new PagingBean();
    }
    private void get_medicine_history(String url, final String tel, int pageIndex, int pageSize)
    {
        BEAN.setPageIndex(pageIndex);
        BEAN.setPageSize(pageSize);
        Log.d("pag", BEAN.getPageIndex()+":"+BEAN.getPageSize());
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_history")
                .params("tel",tel)
                .params("page",BEAN.getPageIndex())
                .params("count",BEAN.getPageSize())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("history", response);
                        JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1)
                        {
                            final JSONObject detail=object.getJSONObject("detail");
                            final int total=detail.getInteger("total");//现在接口中为count
                            BEAN.setTotal(total);
                            //medicineHistoryList.addAll(convert_response_to_history(response));
                            medicineHistoryRecyclerViewAdapter.addData(convert_response_to_history(response));
                            medicineHistoryRecyclerViewAdapter.notifyDataSetChanged();
                            BEAN.addIndex();
                            medicineHistoryRecyclerViewAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                                @Override
                                public void onLoadMoreRequested() {
                                    paging(UploadConfig.API_HOST+"/api/get_history",tel);

                                }
                            },mRecyclerViewHistory);
                        }
                    }
                })
                .build()
                .get();
    }
    private ArrayList<MultipleItemEntity> convert_response_to_history(String jsonString){
        ArrayList<MultipleItemEntity> mDatas=new ArrayList<>();
        if(jsonString!=null) {
            String medicineName=null;
            String medicineUseTime=null;
            int historyType=-1;
            String medicineHistoryType=null;
            String tel=null;
            String boxId=null;
            String id=null;
            JSONObject jsonobject = JSON.parseObject(jsonString);
                JSONObject jsonobject1 = jsonobject.getJSONObject("detail");
                JSONArray jsonarray = jsonobject1.getJSONArray("histories");
                int size = jsonarray.size();
            Log.d("jsonsize", size+"");
                for (int i = 0; i < size; i++) {

                    JSONObject jsonobject2 = jsonarray.getJSONObject(i);
                    boxId=jsonobject2.getString("boxId");
                    medicineName=jsonobject2.getString("medicineNames");
                    historyType=jsonobject2.getIntValue("status");
                    switch (historyType) {
                        case 1:
                            medicineHistoryType = "药盒按时服用:";
                            break;
                        case 2:
                            medicineHistoryType = "药箱按时服用:";
                            break;
                        case 3:
                            medicineHistoryType = "药盒未按时服用:";
                            break;
                        case 4:
                            medicineHistoryType = "药箱未按时服用:";
                            break;
                        case 5:
                            medicineHistoryType = "药盒非服药操作";
                            break;
                        case 6:
                            medicineHistoryType = "药箱非服药操作";
                            break;
                    }
                    medicineUseTime=jsonobject2.getString("medicineUseTime");
                    tel=jsonobject2.getString("tel");
                    id=jsonobject2.getString("id");

                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, ItemType.MEDICINE_HISTORY)
                            .setField(MultipleFields.MEDICINE_NAME,medicineName)
                            .setField(MultipleFields.MEDICINEUSERTIME,medicineUseTime)
                            .setField(MultipleFields.MEDICINEHISTORYTYPE,medicineHistoryType)
                            .setField(MultipleFields.BOXID,boxId)
                            .setField(MultipleFields.TEL,tel)
                            .setField(MultipleFields.ID,id)
                            .build();
                    mDatas.add(entity);

                }
            }
            return mDatas;
        }
    private void paging(final String url,final String tel) {
        //medicineHistoryList.clear();
        final int pageSize = BEAN.getPageSize();
        final int currentCount = BEAN.getCurrentCount();
        final int total = BEAN.getTotal();
        final int pageIndex=BEAN.getPageIndex();

        if (medicineHistoryRecyclerViewAdapter.getData().size() < pageSize || currentCount >= total) {

            medicineHistoryRecyclerViewAdapter.loadMoreEnd(true);
        } else {
            Latte.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RestClient.builder()
                            .url(url)
                            .params("tel",tel)
                            .params("page",BEAN.getPageIndex())
                            .params("count",BEAN.getPageSize())
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    Log.d("aaaa", response);
                                    JSONObject object = JSON.parseObject(response);
                                    int code = object.getIntValue("code");
                                    if (code == 1) {
                                        //medicineHistoryList.clear();
                                        Log.d("last", response);
                                        //medicineHistoryList.addAll(convert_response_to_history(response));
                                        //Log.d("medicineHistoryList", "medicineHistoryList="+medicineHistoryList);
                                        Log.d("pageindex", BEAN.getPageIndex()+"");
                                        Log.d("paging", "pagingsize="+medicineHistoryRecyclerViewAdapter.getData().size()+"");
                                        //Log.d("sizerest","size="+convert_response_to_history(response).size());

                                        medicineHistoryRecyclerViewAdapter.addData(convert_response_to_history(response));
                                        //累加数量
                                        BEAN.setCurrentCount(medicineHistoryRecyclerViewAdapter.getData().size());
                                        medicineHistoryRecyclerViewAdapter.loadMoreComplete();
                                        BEAN.addIndex();
                                        Log.d("getdatasize", "getdatasize="+medicineHistoryRecyclerViewAdapter.getData().size()+"::pagesize="+pageSize+"currentCount="+currentCount+"total=="+total);

                                    }
                                }
                            })
                            .build()
                            .get();
                }
            }, 1000);
        }
    }
}
