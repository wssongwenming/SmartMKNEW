package com.dtmining.latte.ui.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.R;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.ui.banner.BannerCreator;
import com.dtmining.latte.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.ui.sub_delegates.medicine_mine.MedicineMineDelegate;
import com.dtmining.latte.ui.sub_delegates.medicine_take_history.MedicineTakeHistoryDelegate;
import com.dtmining.latte.ui.sub_delegates.medicine_take_plan.MedicineTakePlan;
import com.dtmining.latte.ui.sub_delegates.scan_add.ScanAddDelegate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.DELETE;

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
        return new MultipleRecyclerAdapter(converter.convert(),delegate);
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
    protected void convert(MultipleViewHolder holder, MultipleItemEntity entity) {
        final String text;
        final String medicinename;
        final String atime;
        final String button_name;
        final Integer button_image;
        final String imageUrl;
        final ArrayList<Integer> bannerImages;
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
            case ItemType.TEXT_TEXT:
                medicinename=entity.getField(MultipleFields.MEDICINE_NAME);
                atime=entity.getField(MultipleFields.ATIME);
                holder.setText(R.id.medicine_name,medicinename);
                holder.setText(R.id.medicine_at_time,atime);
                break;

            case ItemType.SEPERATOR:
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
                DELEGATE.start(new ScanAddDelegate());
                break;
            case 2://点击了“手动添加”
                DELEGATE.start(new HandAddDelegate());
                break;
            case 3://点击了“我的药品”
                DELEGATE.start(new MedicineMineDelegate());
                break;
            case 4://点击了“用药计划”
                DELEGATE.start(new MedicineTakePlan());
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
}

