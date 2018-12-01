package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;


import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineMineDelegate extends LatteDelegate implements View.OnClickListener,AdapterView.OnItemClickListener{
    private String tel=null;
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
        mRefreshHandler.firstPage_medicine_mine(UploadConfig.API_HOST+"/api/get_medicine",tel);
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
}
