package com.dtmining.latte.mk.main.aboutme.list;

import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineTakeHistoryDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicineTakePlanDelegate;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/10/21
 * Description:
 */
public class ListAdapter extends BaseMultiItemQuickAdapter<ListBean,BaseViewHolder> {
    private final LatteDelegate DELEGATE;
    private static final RequestOptions OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .dontAnimate()
            .centerCrop();

    public ListAdapter(List<ListBean> data, LatteDelegate delegate) {
        super(data);
        this.DELEGATE=delegate;
        addItemType(ListItemType.ITEM_NORMAL, R.layout.arrow_item_layout);
        addItemType(ListItemType.ITEM_WITH_IMAGE, R.layout.arrow_item_with_image);
        addItemType(ListItemType.ITEM_AVATAR, R.layout.arrow_item_avatar);
        addItemType(ListItemType.ITEM_SWITCH,R.layout.arrow_switch_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListBean item) {
        switch (helper.getItemViewType()) {
            case ListItemType.ITEM_NORMAL:
                helper.setText(R.id.tv_arrow_text, item.getText());
                helper.setText(R.id.tv_arrow_value, item.getValue());

                break;
            case ListItemType.ITEM_WITH_IMAGE:
                helper.setText(R.id.arrow_tv_text_value, item.getText());
                helper.setImageResource(R.id.arrow_img_icon, item.getmImage());
                final int id=item.getId();
                View view=helper.itemView;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showContent(id);
                    }
                });
                break;
            case ListItemType.ITEM_AVATAR:
                Glide.with(mContext)
                        .load(item.getImageUrl())
                        .apply(OPTIONS)
                        .into((ImageView) helper.getView(R.id.img_arrow_avatar));
                break;
            case ListItemType.ITEM_SWITCH:
                helper.setText(R.id.tv_arrow_switch_text,item.getText());
                final SwitchCompat switchCompat = helper.getView(R.id.list_item_switch);
                switchCompat.setChecked(true);
                switchCompat.setOnCheckedChangeListener(item.getmOnCheckedChangeListener());
                break;
            default:
                break;
        }
    }

    private void showContent(int Id){
        final int id=Id;
        switch (id)
        {
            case 1://点击了“扫码添加”
                DELEGATE.startScanWithCheck(DELEGATE);
                Log.d("sm", "扫码添加 ");
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
}
