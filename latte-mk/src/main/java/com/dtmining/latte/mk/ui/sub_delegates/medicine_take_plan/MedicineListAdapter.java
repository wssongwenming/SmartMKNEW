package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.widget.Toast;

import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.adapter.CustomBaseAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListDataConverter;

import java.util.LinkedList;

/**
 * author:songwenming
 * Date:2018/10/23
 * Description:
 */
public class MedicineListAdapter extends CustomBaseAdapter<MedicineModel> {
    public MedicineListAdapter(LinkedList<MedicineModel> mData, int mLayoutRes) {
        super(mData, mLayoutRes);
    }
    public static MedicineListAdapter create(LinkedList<MedicineModel> data, int mLayoutRes){
        return new MedicineListAdapter((LinkedList<MedicineModel>) data,mLayoutRes);
    }
    public static MedicineListAdapter create(MedicineListDataConverter converter, int mLayoutRes){
        return new MedicineListAdapter(converter.convert(),mLayoutRes);
    }
    @Override
    public void bindView(ViewHolder holder, MedicineModel obj) {
        holder.setText(R.id.single_item_tv,obj.toString());

    }
}
