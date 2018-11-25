package com.dtmining.latte.mk.main.aboutme.mymedicineboxes;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bigkoo.convenientbanner.listener.OnItemClickListener;
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
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineRecyclerAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineReactionDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;

import java.util.List;
import java.util.Set;

/**
 * author:songwenming
 * Date:2018/11/4
 * Description:
 */
public class MedicineBoxesMineAdapter extends BaseMultiItemQuickAdapter<MultipleItemEntity,MultipleViewHolder> implements
        OnItemClickListener,BaseQuickAdapter.OnItemChildClickListener {
    LatteDelegate DELEGATE;
    //设置图片加载策略
    private static final RequestOptions REQUEST_OPTIONS=
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();
    public MedicineBoxesMineAdapter(List<MultipleItemEntity> data, LatteDelegate delegate) {
        super(data);
        init();
        this.DELEGATE=delegate;
    }
    public static  MedicineBoxesMineAdapter create(List<MultipleItemEntity>data,LatteDelegate delegate){
        return new  MedicineBoxesMineAdapter(data,delegate);
    }

    public static  MedicineBoxesMineAdapter create(DataConverter converter,LatteDelegate delegate){
        return new  MedicineBoxesMineAdapter(converter.convert(),delegate);
    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    protected void convert(MultipleViewHolder holder, MultipleItemEntity item) {
        final String tel;
        final String overDue;
        final String onUse;
        final String pause;
        final String boxId;
        LinearLayoutCompat view = (LinearLayoutCompat) holder.itemView;
        Button more= (Button) view.findViewById(R.id.btn_medicine_boxes_mine_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        switch (holder.getItemViewType())
        {
            case ItemType.MEDICINE_BOX:
                tel=item.getField(MultipleFields.TEL);
                overDue=item.getField(MultipleFields.OVERDUE);
                onUse=item.getField(MultipleFields.ONUSE);
                pause=item.getField(MultipleFields.PAUSE);
                boxId=item.getField(MultipleFields.BOXID);
                holder.setText(R.id.tv_medicine_boxes_mine_boxid,boxId);
                holder.setText(R.id.tv_medicine_boxes_mine_onuse,"在服："+onUse);
                holder.setText(R.id.tv_medicine_boxes_mine_pause,"暂停："+pause);
                holder.setText(R.id.tv_medicine_boxes_mine_overdue,"过期："+overDue);

                AppCompatButton moreButton =holder.getView(R.id.btn_medicine_boxes_mine_more);
                moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("fanying", "onClick: ");
                        MedicineMineDelegate delegate= MedicineMineDelegate.newInstance(boxId);
                        DELEGATE.start(delegate);
                        //DELEGATE.start(new MedicineTakeHistoryDelegate());

                    }
                });
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }
    private void init(){
        addItemType(ItemType.MEDICINE_BOX, R.layout.item_medicine_boxes_mine);
        //openLoadAnimation();
    }
    @Override
    protected MultipleViewHolder createBaseViewHolder(View view) {
        return MultipleViewHolder.create(view);
    }
    @Override
    public void onBindViewHolder(MultipleViewHolder holder, int positions) {
        super.onBindViewHolder(holder, positions);
    }


}
