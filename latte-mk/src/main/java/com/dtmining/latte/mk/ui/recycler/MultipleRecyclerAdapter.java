package com.dtmining.latte.mk.ui.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.R;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.index.IndexDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.MedicineOverdueDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.model.DeleteOverDue;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.model.MedicineInfo;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_summary.MedicineSummaryConverter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineReactionDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.banner.BannerCreator;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineTakeHistoryDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicineTakePlanDelegate;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/10/2
 * Description:
 */
public class MultipleRecyclerAdapter extends BaseMultiItemQuickAdapter<MultipleItemEntity,MultipleViewHolder>
        implements
        BaseQuickAdapter.SpanSizeLookup,
        OnItemClickListener,BaseQuickAdapter.OnItemChildClickListener{
    //recyclerview 的机制是滑动下来再滑动上去，就重新加载一次数据，这里确保只初始化一次Banner，防止重复Item加载
    private final LatteDelegate DELEGATE;
    private String doseUnit;
    private boolean mIsInitBanner=false;
    //设置图片加载策略
    private static final RequestOptions REQUEST_OPTIONS=
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    protected MultipleRecyclerAdapter(List<MultipleItemEntity> data, LatteDelegate delegate) {
        super(data);
        this.DELEGATE=delegate;
        init();
    }



    public static MultipleRecyclerAdapter create(List<MultipleItemEntity> data,LatteDelegate delegate){
        return new MultipleRecyclerAdapter(data,delegate);
    }
    public static MultipleRecyclerAdapter create(DataConverter converter,LatteDelegate delegate){
        return new MultipleRecyclerAdapter(converter.getEntities(),delegate);
    }
    public static MultipleRecyclerAdapter getMedicineHistory(DataConverter converter,LatteDelegate delegate){
        return new MultipleRecyclerAdapter(((IndexDataConverter)converter).getMedicineHistory(),delegate);
    }

    public static MultipleRecyclerAdapter getMedicineHistoryForDetail(DataConverter converter,LatteDelegate delegate){
        return new MultipleRecyclerAdapter(((IndexDataConverter)converter).convertMedicineHistoryDetail(),delegate);
    }
    public static MultipleRecyclerAdapter getMedicineOverdue(DataConverter converter,LatteDelegate delegate)
    {
        return new MultipleRecyclerAdapter(((MedicineOverdueDataConverter)converter).getMedicineOverdue(),delegate);
    }
    public static MultipleRecyclerAdapter getMedicineSummary(DataConverter converter,LatteDelegate delegate)
    {
        return new MultipleRecyclerAdapter(((MedicineSummaryConverter)converter).getMedicineSummary(),delegate);
    }
    public static MultipleRecyclerAdapter getTop(DataConverter converter,LatteDelegate delegate){
        return new MultipleRecyclerAdapter(converter.getTop(),delegate);
    }
    public static MultipleRecyclerAdapter getHistoryMore(DataConverter converter,LatteDelegate delegate){
        return new MultipleRecyclerAdapter(((IndexDataConverter)converter).getMedicineHistoryMore(),delegate);
    }

    private void init(){
        //设置不同的item布局
        addItemType(ItemType.TEXT, R.layout.item_mutiple_text);
        addItemType(ItemType.IMAGE_TEXT_COMMAND_BUTTON,R.layout.item_multiple_image_text_for_command_button);
        addItemType(ItemType.SEPERATOR,R.layout.item_mutiple_seperator_line);
        addItemType(ItemType.TEXT_MORE_FOR_TAKE_MEDICINE_HISTORY,R.layout.item_multiple_more_text_for_medicine_history);
        addItemType(ItemType.TEXT_MORE_FOR_TAKE_MEDICINE_PLAN,R.layout.item_multiple_more_text_for_medicine_plan);
        addItemType(ItemType.IMAGE,R.layout.item_multiple_image);
        addItemType(ItemType.TEXT_IMAGE,R.layout.item_multiple_image_text);
        addItemType(ItemType.BANNER,R.layout.item_multiple_banner);
        addItemType(ItemType.TEXT_TEXT,R.layout.item_mutiple_text_text);
        addItemType(ItemType.MEDICINE_OVER_DUE,R.layout.item_medicine_overdue);
        addItemType(ItemType.MEDICINE_HISTORY,R.layout.item_medicine_history);
        addItemType(ItemType.MEDICINE_SUMMARY,R.layout.item_medicine_summary);
        addItemType(ItemType.MEDICINE_USE_MESSAGE,R.layout.item_user_message);
        addItemType(ItemType.MEDICINE_OVERDUE_MESSAGE,R.layout.item_user_message);
        addItemType(ItemType.MEDICINE_SUPPLY_MESSAGE,R.layout.item_user_message);
        addItemType(ItemType.BOXLIST,R.layout.item_my_boxid);
        //设置宽度监听,只要参数实现了SpanSizeLookup接口
        setSpanSizeLookup(this);
        openLoadAnimation();
        //多次执行动画
        isFirstOnly(false);
    }


/*   @Override
    protected void convert(MultipleViewHolder holder, MultipleItemEntity entity) {
        final String text;
        final String imageUrl;
        final ArrayList<String> bannerImages;
        switch (holder.getItemViewType())
        {
            case ItemType.TEXT:
                text=entity.getField(MultipleFields.TEXT);
                holder.setText(R.id.text_single,text);
                break;
            case ItemType.IMAGE:
                imageUrl=entity.getField(MultipleFields.IMAGE_URL);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) holder.getView(R.id.img_single));
                break;
            case ItemType.TEXT_IMAGE:
                text=entity.getField(MultipleFields.TEXT);
                imageUrl=entity.getField(MultipleFields.IMAGE_URL);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) holder.getView(R.id.img_multiple));
                holder.setText(R.id.tv_multiple,text);
                break;
            case ItemType.BANNER:
                if(!mIsInitBanner)
                {
                    bannerImages=entity.getField(MultipleFields.BANNERS);
                    final ConvenientBanner<String> convenientBanner=holder.getView(R.id.banner_recycler_item);
                    BannerCreator.setDefault(convenientBanner,bannerImages,this);
                    mIsInitBanner=true;
                }
                break;
            default:
                break;
        }
    }*/
@Override
protected MultipleViewHolder createBaseViewHolder(View view) {
    return MultipleViewHolder.create(view);
}
    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        final String text;
        final String medicineName;
        final String medicineHistoryType;
        final String medicineId;
        final int medicineType;
        final String medicineUseTime;
        final String boxId;
        final String tel;
        final String validity;
        final String medicineCount;
        final String button_name;
        final Integer button_image;
        final String imageUrl;
        final ArrayList<Integer> bannerImages;
        final int medicineUsertime;
        final int medicineUserCount;
        switch (holder.getItemViewType())
        {
            case ItemType.TEXT:
                text=entity.getField(MultipleFields.TEXT);
                holder.setText(R.id.text_single,text);
                break;
            case ItemType.IMAGE:
                imageUrl=entity.getField(MultipleFields.IMAGE_URL);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) holder.getView(R.id.img_single));
                break;

            case ItemType.TEXT_IMAGE:
                text=entity.getField(MultipleFields.TEXT);
                imageUrl=entity.getField(MultipleFields.IMAGE_URL);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) holder.getView(R.id.img_multiple));
                holder.setText(R.id.tv_multiple,text);
                break;
            case ItemType.IMAGE_TEXT_COMMAND_BUTTON:
                button_name=entity.getField(MultipleFields.BUTTON_NAME);
                button_image=entity.getField(MultipleFields.BUTTON_IMAGE);
                holder.setText(R.id.command_button_text,button_name);
                holder.setImageResource(R.id.command_button_icon,button_image);
                final int id=entity.getField(MultipleFields.ID);

                View view =holder.itemView;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showContent(id);
                    }
                });
                break;
            case ItemType.TEXT_TEXT://首页的用药历史
                medicineName=entity.getField(MultipleFields.MEDICINE_NAME);
                medicineUseTime=entity.getField(MultipleFields.MEDICINEUSERTIME);
                holder.setText(R.id.medicine_name,medicineName);
                holder.setText(R.id.medicine_at_time,medicineUseTime);
                break;

            case ItemType.SEPERATOR:
                break;
            case ItemType.TEXT_MORE_FOR_TAKE_MEDICINE_HISTORY:
                View view_more_medicine =holder.getView(R.id.tv_item_medicine_history_more);
                view_more_medicine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DELEGATE.start(new MedicineTakeHistoryDelegate());

                    }
                });
                break;
            case ItemType.MEDICINE_OVER_DUE:
                medicineName=entity.getField(MultipleFields.MEDICINENAME);
                medicineCount=entity.getField(MultipleFields.MEDICINECOUNT);
                validity=entity.getField(MultipleFields.MEDICINEVALIDITY);
                medicineType=entity.getField(MultipleFields.MEDICINETYPE);
                imageUrl=entity.getField(MultipleFields.MEDICINEIMGURL);
                medicineId=entity.getField(MultipleFields.MEDICINEID);
                boxId=entity.getField(MultipleFields.BOXID);
                tel=entity.getField(MultipleFields.TEL);
                switch (medicineType) {
                    case 0:
                        doseUnit = "片";
                        break;
                    case 1:
                        doseUnit = "粒/颗";
                        break;
                    case 2:
                        doseUnit = "瓶/支";
                        break;
                    case 3:
                        doseUnit = "包";
                        break;
                    case 4:
                        doseUnit = "克";
                        break;
                    case 5:
                        doseUnit = "毫升";
                        break;
                    case 6:
                        doseUnit = "其他";
                        break;
                }
                holder.setText(R.id.tv_medicine_over_due_medicine_name,medicineName);
                holder.setText(R.id.tv_medicine_over_due_medicine_count,"剩余"+medicineCount+doseUnit);
                holder.setText(R.id.tv_medicine_over_due_validity_time,validity);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(REQUEST_OPTIONS)
                        .into((ImageView) holder.getView(R.id.iv_medicine_over_due_medicine_pic));
                View delete=holder.getView(R.id.btn_medicine_over_due_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MedicineInfo medicineInfo=new MedicineInfo();
                        DeleteOverDue deleteOverDue=new DeleteOverDue();
                        medicineInfo.setBoxId(boxId);
                        medicineInfo.setMedicineId(medicineId);
                        medicineInfo.setTel(tel);
                        deleteOverDue.setDetail(medicineInfo);
                        String deleteString =  JSON.toJSON(deleteOverDue).toString();
                        RestClient.builder().clearParams()
                                .url(UploadConfig.API_HOST+"/api/Medicine_delete ")//
                                .raw(deleteString)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        final int currentPosition = holder.getAdapterPosition();
                                        getData().remove(holder.getAdapterPosition());
                                        notifyItemRemoved(currentPosition);
                                       // notifyDataSetChanged();
                                        notifyItemRangeChanged(currentPosition,getData().size());
                                    }
                                })
                                .build()
                                .post();

                    }
                });
                break;
            case ItemType.MEDICINE_HISTORY://这里是单独的用药历史列表,不同于首页的用药历史
                medicineName=entity.getField(MultipleFields.MEDICINE_NAME);
                medicineHistoryType=entity.getField(MultipleFields.MEDICINEHISTORYTYPE);
                medicineUseTime=entity.getField(MultipleFields.MEDICINEUSERTIME);
                final String historyId=entity.getField(MultipleFields.ID);
                holder.setText(R.id.tv_medicine_history_medicine_time,medicineUseTime);
                holder.setText(R.id.tv_medicine_history_medicine_name,medicineName);
                holder.setText(R.id.tv_medicine_history_medicine_history_type,medicineHistoryType);
                Button button =holder.getView(R.id.btn_medicine_history_medicine_use_reflect);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("fanying", "onClick: ");
                        MedicineReactionDelegate delegate=MedicineReactionDelegate.newInstance(historyId);
                        DELEGATE.start(delegate);
                        //DELEGATE.start(new MedicineTakeHistoryDelegate());

                    }
                });
                break;
            case ItemType.TEXT_MORE_FOR_TAKE_MEDICINE_PLAN:
                View view_more_medicine_plan =holder.getView(R.id.tv_item_medicine_plan_more);
                view_more_medicine_plan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DELEGATE.start(new MedicineTakePlanDelegate());

                    }
                });
                break;
            case ItemType.BANNER:
                if(!mIsInitBanner)
                {
                    bannerImages=entity.getField(MultipleFields.BANNERS_INTEGER);
                    final ConvenientBanner<Integer> convenientBanner=holder.getView(R.id.banner_recycler_item);
                    //BannerCreator.setInteger(convenientBanner,bannerImages,this);
                    BannerCreator.setInteger(convenientBanner,bannerImages);

                    mIsInitBanner=true;
                }
                break;
            case ItemType.MEDICINE_USE_MESSAGE:
                String title=entity.getField(MultipleFields.MEDICINE_USE_MESSAGE_TITLE);
                String time=entity.getField(MultipleFields.MEDICINE_USE_MESSAGE_TIME);
                String content=entity.getField(MultipleFields.MEDICINE_USE_MESSAGE_CONTENT);
                Log.d("use", title+":"+time+":"+content);
                holder.setText(R.id.tv_message_title,title);
                holder.setText(R.id.tv_message_time,time.toString());
                holder.setText(R.id.tv_message_content,content);
                break;
            case ItemType.MEDICINE_OVERDUE_MESSAGE:
                String title1=entity.getField(MultipleFields.MEDICINE_OVERDUE_MESSAGE_TITLE);
                String time1=entity.getField(MultipleFields.MEDICINE_OVERDUE_MESSAGE_TIME);
                String content1=entity.getField(MultipleFields.MEDICINE_OVERDUE_MESSAGE_CONTENT);
                holder.setText(R.id.tv_message_title,title1);
                holder.setText(R.id.tv_message_time, time1.toString());
                holder.setText(R.id.tv_message_content,content1);
                break;
            case ItemType.MEDICINE_SUPPLY_MESSAGE:
                String title2=entity.getField(MultipleFields.MEDICINE_SUPPLY_MESSAGE_TITLE);
                String time2=entity.getField(MultipleFields.MEDICINE_SUPPLY_MESSAGE_TIME);
                String content2=entity.getField(MultipleFields.MEDICINE_SUPPLY_MESSAGE_CONTENT);
                holder.setText(R.id.tv_message_title,title2);
                holder.setText(R.id.tv_message_time, time2.toString());
                holder.setText(R.id.tv_message_content,content2);
                break;
            case ItemType.MEDICINE_SUMMARY:
                medicineUserCount=entity.getField(MultipleFields.MEDICINE_USERCOUNT);
                medicineUsertime=entity.getField(MultipleFields.MEDICINE_USERTIME);
                medicineName=entity.getField(MultipleFields.MEDICINE_NAME);
                medicineType=entity.getField(MultipleFields.MEDICINETYPE);
                switch (medicineType) {
                    case 0:
                        doseUnit = "片";
                        break;
                    case 1:
                        doseUnit = "粒/颗";
                        break;
                    case 2:
                        doseUnit = "瓶/支";
                        break;
                    case 3:
                        doseUnit = "包";
                        break;
                    case 4:
                        doseUnit = "克";
                        break;
                    case 5:
                        doseUnit = "毫升";
                        break;
                    case 6:
                        doseUnit = "其他";
                        break;
                }
                holder.setText(R.id.tv_item_medicine_summary_medicine_name,medicineName);
                holder.setText(R.id.tv_item_medicine_summary_medicine_use_time,medicineUsertime+"天"+"服用："+medicineUserCount+doseUnit);
                break;
            case ItemType.BOXLIST:
                boxId=entity.getField(MultipleFields.BOXID);
                holder.setText(R.id.tv_my_box,boxId);

                break;
            default:
                break;
        }
    }
    @Override
    public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
        return getData().get(position).getField(MultipleFields.SPAN_SIZE);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        //判断id
/*        if (view.getId() == R.id.iv_img) {
            Log.i("tag", "点击了第" + position + "条条目的 图片");
        } else if (view.getId() == R.id.tv_title) {
            Log.i("tag", "点击了第" + position + "条条目的 标题");
        }*/

    }

    private void showContent(int Id){
        final int id=Id;
        switch (id)
        {
            case 1://点击了“扫码添加”
                DELEGATE.startScanWithCheck(DELEGATE);
                break;
            case 2://点击了“手动添加”
                DELEGATE.start(new HandAddDelegate());
                break;
            case 3://点击了“我的药品”
                DELEGATE.start(new MedicineMineDelegate());
                break;
            case 4://点击了“用药计划”
                DELEGATE.start(new MedicineTakePlanDelegate());
                break;
            case 5://点击了“用药记录”
                DELEGATE.start(new MedicineTakeHistoryDelegate());
                break;
        }
    }
    private void switchContent(LatteDelegate delegate){

        //if(contentDelegate!=null){
        //    contentDelegate.replaceFragment(delegate,false);
        }

    @Override
    public void onItemClick(int position) {

    }
    /**
     *
        * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + "天 " + hours + "小时 ";
    }
}

