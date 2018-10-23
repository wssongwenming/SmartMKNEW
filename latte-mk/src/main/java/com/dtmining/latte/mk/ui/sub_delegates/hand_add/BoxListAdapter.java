package com.dtmining.latte.mk.ui.sub_delegates.hand_add;

import android.widget.SpinnerAdapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.adapter.CustomBaseAdapter;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/10/23
 * Description:
 */
public class BoxListAdapter extends CustomBaseAdapter<String> {
    public BoxListAdapter(LinkedList<String> mData, int mLayoutRes) {
        super(mData, mLayoutRes);
    }



    public static BoxListAdapter create(LinkedList<String> data,int mLayoutRes){
        return new BoxListAdapter((LinkedList<String>) data,mLayoutRes);
    }

    public static BoxListAdapter create(BoxListDataConverter converter,int mLayoutRes){
        return new BoxListAdapter(converter.convert(),mLayoutRes);
    }






    @Override
    public void bindView(ViewHolder holder, String obj) {
        holder.setText(R.id.single_item_tv,obj);
    }
}
