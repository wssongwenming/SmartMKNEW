package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;

import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleViewHolder;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineStateModel;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.List;
import java.util.Set;

/**
 * author:songwenming
 * Date:2018/10/25
 * Description:
 */
public class MedicineMineRecyclerAdapter extends BaseMultiItemQuickAdapter<MultipleItemEntity,MultipleViewHolder> implements
         OnItemClickListener,BaseQuickAdapter.OnItemChildClickListener {
    AppCompatTextView mMedicineInUse=null;
    private  LatteDelegate DELEGATE;
    Set<SwipeListLayout> sets=null;


    //设置图片加载策略
    private static final RequestOptions REQUEST_OPTIONS=
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    protected MedicineMineRecyclerAdapter(List<MultipleItemEntity> data,Set<SwipeListLayout> sets) {
        super(data);
        init();

        this.sets=sets;
    }

    public static MedicineMineRecyclerAdapter create(List<MultipleItemEntity>data,Set<SwipeListLayout> sets){
        return new MedicineMineRecyclerAdapter(data,sets);
    }

    public static MedicineMineRecyclerAdapter create(DataConverter converter,Set<SwipeListLayout> sets){
        return new MedicineMineRecyclerAdapter(converter.convert(),sets);
    }
    @Override
    protected void convert(final MultipleViewHolder holder, final MultipleItemEntity item) {
        final String tel;
        final int medicinePause;
        final int medicineCount;
        final String medicineId;
        final String medicineName;
        final String medicineImageUrl;
        final String boxId;
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
        //动作
        AppCompatTextView mBtnSupply=(AppCompatTextView) view.findViewById(R.id.tv_btn_item_medicine_mine_supply);


        switch (holder.getItemViewType())
        {

            case ItemType.MEDICINE_MINE:
                medicinePause   =item.getField(MultipleFields.MEDICINEPAUSE);
                medicineCount   =item.getField(MultipleFields.MEDICINECOUNT);
                medicineId      =item.getField(MultipleFields.MEDICINEID);
                medicineName    =item.getField(MultipleFields.MEDICINENAME);
                medicineImageUrl=item.getField(MultipleFields.MEDICINEIMGURL);
                boxId           =item.getField(MultipleFields.BOXID);
                tel=            item.getField(MultipleFields.TEL);
                Glide.with(mContext)
                        .load(medicineImageUrl)
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
                                .url("http://10.0.2.2:8081/Web01_exec/Medicine_update_state")
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
                        String singInJson = JSON.toJSON(medicineStateModel).toString();
                        RestClient.builder()
                                .url("http://10.0.2.2:8081/Web01_exec/Medicine_update_state")
                                .raw(singInJson)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        final int currentPosition = holder.getAdapterPosition();
                                           /*
                                            //还原上一个
                                            getData().get(mPrePosition).setField(MultipleFields.TAG, false);
                                            notifyItemChanged(mPrePosition);*/
                                        //更新选中的item
                                        getData().remove(holder.getAdapterPosition());
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



}
