package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;

import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleViewHolder;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineModel;
import com.dtmining.latte.mk.ui.sub_delegates.views.InputDialog;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineStateModel;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Set;

/**
 * author:songwenming
 * Date:2018/10/25
 * Description:
 */
public class MedicineMineRecyclerAdapter extends BaseMultiItemQuickAdapter<MultipleItemEntity,MultipleViewHolder> implements
         OnItemClickListener,BaseQuickAdapter.OnItemChildClickListener,InputDialog.ClickListenerInterface {
    AppCompatTextView mMedicineInUse=null;
    private  LatteDelegate DELEGATE;
    private  String MEDICINEID;
    private  int MEDICINE_ORIGN_COUNT=0;//补充时需要加上原来的数量
    private  int POSITION;
    Set<SwipeListLayout> sets=null;
    private InputDialog inputDialog;

    //设置图片加载策略
    private static final RequestOptions REQUEST_OPTIONS=
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    public MedicineMineRecyclerAdapter(List<MultipleItemEntity> data, Set<SwipeListLayout> sets,LatteDelegate delegate) {
        super(data);
        this.DELEGATE=delegate;
        init();
        this.sets=sets;
    }

    public static MedicineMineRecyclerAdapter create(List<MultipleItemEntity>data,Set<SwipeListLayout> sets,LatteDelegate latteDelegate){
        return new MedicineMineRecyclerAdapter(data,sets,latteDelegate);
    }

    public static MedicineMineRecyclerAdapter create(DataConverter converter,Set<SwipeListLayout> sets,LatteDelegate latteDelegate){
        return new MedicineMineRecyclerAdapter(converter.convert(),sets,latteDelegate);
    }
    @Override
    protected void convert(final MultipleViewHolder holder, final MultipleItemEntity item) {
        final String boxId;
        final int interval;
        final String endRemindTime;
        final String medicineCode;
        final int medicineCount;
        final String medicineImage;
        final int medicineType;
        final String medicineName;
        final int medicineUseCount;
        final String medicineValidity;
        final String startRemindTime;
        final String tel;
        final int timesOnDay;
        final int medicinePause;
        final String medicineId;
        final String medicineImageUrl;

        LinearLayoutCompat view = (LinearLayoutCompat) holder.itemView;
        SwipeListLayout swipeListLayout=((SwipeListLayout)view.findViewById(R.id.itemview_swipe));
        swipeListLayout.setOnSwipeStatusListener(new onSlipStatusListener(swipeListLayout));
        //在服状态
        AppCompatTextView mMedicineInUse= (AppCompatTextView) view.findViewById(R.id.tv_item_medicine_mine_in_use);
        //过期状态
        AppCompatTextView mMedicineOverdue=(AppCompatTextView) view.findViewById(R.id.tv_item_medicine_mine_overdue);
        //已暂停状态
        AppCompatTextView mMedicinePause=(AppCompatTextView) view.findViewById(R.id.tv_item_medicine_mine_pause);

        //暂停动作
        AppCompatTextView mBtnPause= (AppCompatTextView) view.findViewById(R.id.tv_btn_item_medicine_mine_pause);
        //开始动作
        AppCompatTextView  mBtnStart = (AppCompatTextView) view.findViewById(R.id.tv_btn_item_medicine_mine_start);
        //删除动作
        AppCompatTextView mBtnDelete=(AppCompatTextView) view.findViewById(R.id.tv_btn_item_medicine_mine_delete);
        //补充动作
        AppCompatTextView mBtnSupply=(AppCompatTextView) view.findViewById(R.id.tv_btn_item_medicine_mine_supply);
        //动作
        AppCompatTextView mBtnEdit=(AppCompatTextView) view.findViewById(R.id.tv_btn_item_medicine_mine_edit);

        switch (holder.getItemViewType())
        {
            case ItemType.MEDICINE_MINE:
                endRemindTime   =item.getField(MultipleFields.MEDICINEENDREMIND);
                medicineCode    =item.getField(MultipleFields.MEDICINECODE);
                medicineValidity=item.getField(MultipleFields.MEDICINEVALIDITY);
                medicineId      =item.getField(MultipleFields.MEDICINEID);
                medicineCount   =item.getField(MultipleFields.MEDICINECOUNT);
                medicineType    =item.getField(MultipleFields.MEDICINETYPE);
                timesOnDay      =item.getField(MultipleFields.MEDICINETIMESONDAY);
                interval        =item.getField(MultipleFields.MEDICINEINTERVAL);
                startRemindTime =item.getField(MultipleFields.MEDICINESTARTREMIND);
                medicineName    =item.getField(MultipleFields.MEDICINENAME);
                medicineImageUrl=item.getField(MultipleFields.MEDICINEIMGURL);
                boxId           =item.getField(MultipleFields.BOXID);
                medicineUseCount=item.getField(MultipleFields.MEDICINEUSECOUNT);
                medicinePause   =item.getField(MultipleFields.MEDICINEPAUSE);
                tel=            item.getField(MultipleFields.TEL);
                Glide.with(mContext)
                        .load(UploadConfig.UPLOAD_IMG+medicineImageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) (view.findViewById(R.id.iv_item_medicine_mine)));
                        holder.setText(R.id.tv_item_medicine_mine_medicine_name,medicineName);
                ((TextView)(view.findViewById(R.id.tv_item_medicine_mine_medicine_count))).setText("剩余"+medicineCount);
                if(medicinePause==0){//药品状态，0：在服
                    mMedicineInUse.setVisibility(View.VISIBLE);
                    mMedicineOverdue.setVisibility(View.GONE);
                    mMedicinePause.setVisibility(View.GONE);

                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnStart.setVisibility(View.GONE);

                }else if(medicinePause==1){//、1：暂停
                    mMedicinePause.setVisibility(View.VISIBLE);
                    mMedicineInUse.setVisibility(View.GONE);
                    mMedicineOverdue.setVisibility(View.GONE);

                    mBtnPause.setVisibility(View.GONE);
                    mBtnStart.setVisibility(View.VISIBLE);

                }
                else if(medicinePause==2){//2：过期
                    mMedicineOverdue.setVisibility(View.VISIBLE);
                    mMedicineInUse.setVisibility(View.GONE);
                    mMedicinePause.setVisibility(View.GONE);

                    mBtnDelete.setVisibility(View.VISIBLE);
                    mBtnPause.setVisibility(View.GONE);
                    mBtnStart.setVisibility(View.GONE);
                }
                mBtnSupply.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        inputDialog = new InputDialog((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY), "输入补充数量" , "确定",MedicineMineRecyclerAdapter.this);
                        MEDICINEID=medicineId;
                        POSITION=holder.getAdapterPosition();
                        MEDICINE_ORIGN_COUNT=medicineCount;
                        inputDialog.show();
                    }
                });
                mBtnPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MedicineState medicineState=new MedicineState();
                        MedicineStateModel medicineStateModel=new MedicineStateModel();
                        medicineState.setBoxId(LattePreference.getBoxId());
                        medicineState.setMedicineId(medicineId);
                        medicineState.setTel(tel);
                        medicineState.setMedicinePause("1");
                        medicineStateModel.setDetail(medicineState);
                        String json = JSON.toJSON(medicineStateModel).toString();

                        RestClient.builder()
                                .clearParams()
                                .url(UploadConfig.API_HOST+"/api/Medicine_update_state")
                                .raw(json)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        final int currentPosition = holder.getAdapterPosition();
                                           /*
                                            //还原上一个
                                            getData().get(mPrePosition).setField(MultipleFields.TAG, false);
                                            notifyItemChanged(mPrePosition);*/
                                            //更新选中的item
                                            item.setField(MultipleFields.MEDICINEPAUSE, 1);
                                            notifyItemChanged(currentPosition);
                                    }

                                })
                                .error(new IError() {
                                    @Override
                                    public void onError(int code, String msg) {

                                    }
                                })
                                .build()
                                .post();
                    }
                });
                mBtnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MedicineState medicineState=new MedicineState();
                        MedicineStateModel medicineStateModel=new MedicineStateModel();
                        medicineState.setBoxId(LattePreference.getBoxId());
                        medicineState.setMedicineId(medicineId);
                        medicineState.setTel(tel);
                        medicineState.setMedicinePause("0");
                        medicineStateModel.setDetail(medicineState);
                        String json = JSON.toJSON(medicineStateModel).toString();
                        RestClient.builder()
                                .clearParams()
                                .url(UploadConfig.API_HOST+"/api/Medicine_update_state")
                                .raw(json)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        final int currentPosition = holder.getAdapterPosition();
                                           /*
                                            //还原上一个
                                            getData().get(mPrePosition).setField(MultipleFields.TAG, false);
                                            notifyItemChanged(mPrePosition);*/
                                        //更新选中的item
                                        item.setField(MultipleFields.MEDICINEPAUSE, 0);
                                        notifyItemChanged(currentPosition);
                                    }

                                })
                                .error(new IError() {
                                    @Override
                                    public void onError(int code, String msg) {

                                    }
                                })
                                .build()
                                .post();

                    }
                });
                mBtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MedicineState medicineState=new MedicineState();
                        MedicineStateModel medicineStateModel=new MedicineStateModel();
                        medicineState.setBoxId(LattePreference.getBoxId());
                        medicineState.setMedicineId(medicineId);
                        medicineStateModel.setDetail(medicineState);
                        JsonObject detail=new JsonObject();
                        detail.addProperty("medicineId",medicineId);
                        JsonObject deleteJson=new JsonObject();
                        deleteJson.add("detail",detail);
                        String delete = deleteJson.toString();
                        Log.d("delete", delete);
                        RestClient.builder()
                                .clearParams()
                                .url(UploadConfig.API_HOST+"/api/Medicine_delete")
                                .raw(delete)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        final int currentPosition = holder.getAdapterPosition();
                                        mData.remove(holder.getAdapterPosition());
                                        notifyDataSetChanged();
                                        //删除药物后计划也会删除，用回调刷新页面
                                        final IGlobalCallback<String> UpdatePlanCallback_for_index = CallbackManager
                                                .getInstance()
                                                .getCallback(CallbackType.ON_GET_MEDICINE_PLAN_INDEX);
                                        if (UpdatePlanCallback_for_index!= null) {
                                            UpdatePlanCallback_for_index.executeCallback("");
                                        }
                                        final IGlobalCallback<String> UpdatePlanCallback = CallbackManager
                                                .getInstance()
                                                .getCallback(CallbackType.ON_GET_MEDICINE_PLAN);
                                        if (UpdatePlanCallback != null) {
                                            UpdatePlanCallback.executeCallback("");
                                        }
                                     }

                                })
                                .error(new IError() {
                                    @Override
                                    public void onError(int code, String msg) {

                                    }
                                })
                                .build()
                                .post();
                    }
                });
                mBtnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MedicineModel medicineModel=new MedicineModel();
                        medicineModel.setBoxId(boxId);
                        medicineModel.setMedicineName(medicineName);
                        medicineModel.setMedicineCode(medicineCode);
                        medicineModel.setMedicineValidity(medicineValidity);
                        medicineModel.setStartRemind(startRemindTime);
                        medicineModel.setEndRemind(endRemindTime);
                        medicineModel.setMedicineType(medicineType);
                        medicineModel.setDayInterval(interval);
                        medicineModel.setMedicineCount(medicineCount);
                        medicineModel.setTimesOnDay(timesOnDay);
                        medicineModel.setMedicineId(medicineId);
                        medicineModel.setMedicineUseCount(medicineUseCount);
                        medicineModel.setMedicineImage(medicineImageUrl);
                        medicineModel.setTel(tel);
                        Latte.getConfigurator().withMedicineMineDelegate(DELEGATE);
                        DELEGATE.start(MedicineMineEditDelegate.newInstance(medicineModel));

                    }
                });
                break;
            case ItemType.TYPE_FOOT:
                break;
            default:
                break;

        }
    }

    @Override
    protected MultipleViewHolder createBaseViewHolder(View view) {
        return MultipleViewHolder.create(view);
    }


    private void init(){
        addItemType(ItemType.MEDICINE_MINE, R.layout.item_medicine_mine);
        openLoadAnimation();
    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    class onSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {
        private SwipeListLayout slipListLayout;

        public onSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }
        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }
        @Override
        public void onStartCloseAnimation() {
        }
        @Override
        public void onStartOpenAnimation() {

        }


    }

    @Override
    public void onBindViewHolder(MultipleViewHolder holder, int positions) {
        super.onBindViewHolder(holder, positions);
    }

    @Override
    public void doConfirm(String input) {
        System.out.println(input);
        try {
            final int supply = Integer.parseInt(input);
            Log.d("input", supply+"");
            JsonObject detail=new JsonObject();
            detail.addProperty("medicineId",MEDICINEID);
            detail.addProperty("supplynumber",supply+MEDICINE_ORIGN_COUNT);
            JsonObject subjson=new JsonObject();
            subjson.add("detail",detail);
            Log.d("sup",  subjson.toString());
            RestClient.builder()
                    .clearParams()
                    .url(UploadConfig.API_HOST+"/api/Medicine_update_supply")
                    .raw(subjson.toString())

                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d("res1122", response);
                            getItem(POSITION).setField(MultipleFields.MEDICINECOUNT,supply+MEDICINE_ORIGN_COUNT);
                            notifyDataSetChanged();
                            inputDialog.dismiss();
                        }
                    })
                    .build()
                    .post();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        inputDialog.dismiss();
    }
}
