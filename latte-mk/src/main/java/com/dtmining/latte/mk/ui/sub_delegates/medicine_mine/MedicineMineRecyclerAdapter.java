package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.dtmining.latte.mk.ui.sub_delegates.SwipeListLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author:songwenming
 * Date:2018/10/25
 * Description:
 */
public class MedicineMineRecyclerAdapter extends BaseMultiItemQuickAdapter<MultipleItemEntity,MultipleViewHolder> implements
         OnItemClickListener,BaseQuickAdapter.OnItemChildClickListener {
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
    protected void convert(MultipleViewHolder holder, MultipleItemEntity item) {
        final int medicinePause;
        final int medicineCount;
        final String medicineId;
        final String medicineName;
        final String medicineImageUrl;
        final String boxId;
        LinearLayoutCompat view = (LinearLayoutCompat) holder.itemView;
         SwipeListLayout swipeListLayout=((SwipeListLayout)view.findViewById(R.id.itemview_swipe));
        swipeListLayout.setOnSwipeStatusListener(new onSlipStatusListener(swipeListLayout));

        switch (holder.getItemViewType())
        {

            case ItemType.MEDICINE_MINE:
                medicinePause   =item.getField(MultipleFields.MEDICINEPAUSE);
                medicineCount   =item.getField(MultipleFields.MEDICINECOUNT);
                medicineId      =item.getField(MultipleFields.MEDICINEID);
                medicineName    =item.getField(MultipleFields.MEDICINENAME);
                medicineImageUrl=item.getField(MultipleFields.MEDICINEIMGURL);
                boxId           =item.getField(MultipleFields.BOXID);
                Glide.with(mContext)
                        .load(medicineImageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) (view.findViewById(R.id.iv_item_medicine_mine)));
                        holder.setText(R.id.tv_item_medicine_mine_medicine_name,medicineName);
                ((TextView)(view.findViewById(R.id.tv_item_medicine_mine_medicine_count))).setText("aaaa");
                Log.d("zuida", view.findViewById(R.id.itemview_swipe).toString()+"11ddd"  );

               // holder.setText(R.id.tv_item_medicine_mine_medicine_count,medicineCount);
/*                if(medicinePause==0){//药品状态，0：在服、1：暂停、2：过期
                    view.findViewById(R.id.tv_item_medicine_mine_in_use).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_item_medicine_mine_overdue).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_item_medicine_mine_pause).setVisibility(View.GONE);

                    view.findViewById(R.id.tv_btn_item_medicine_mine_pause).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_btn_item_medicine_mine_start).setVisibility(View.GONE);

                }else if(medicinePause==1){
                    view.findViewById(R.id.tv_item_medicine_mine_in_use).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_item_medicine_mine_overdue).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_item_medicine_mine_pause).setVisibility(View.VISIBLE);

                    view.findViewById(R.id.tv_btn_item_medicine_mine_pause).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_btn_item_medicine_mine_start).setVisibility(View.VISIBLE);

                }
                else if(medicinePause==2){
                    view.findViewById(R.id.tv_item_medicine_mine_in_use).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_item_medicine_mine_overdue).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_item_medicine_mine_pause).setVisibility(View.GONE);
                }*/
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
}
