package com.dtmining.latte.mk.main.index;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.MedicinePlanInfo;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.OnClickDeleteListener;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/12/11
 * Description:
 */
public class MyElvAdapterForIndex extends BaseExpandableListAdapter {
    private Context context;
    private ListView listView;
    List<MedicinePlanInfo> list;

    public MyElvAdapterForIndex(Context context, ListView listView, List<MedicinePlanInfo> list) {
        super();
        this.context = context;
        this.listView = listView;
        this.list=list;
    }

    @Override
    public Object getChild(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return list.get(arg0).getDatas().get(arg1);
    }


    @Override
    public long getChildId(int arg0, int arg1) {
        return getCombinedChildId(arg0,arg1);
    }

    @Override
    public View getChildView(final int groupPosition, final int position,
                             boolean arg2, View convertView, ViewGroup parent) {

        final ViewHolder1 viewHolder1;
        if (convertView == null){

            LayoutInflater inflater = ((Activity)Latte.getConfiguration(ConfigKeys.ACTIVITY)).getLayoutInflater();
            convertView= inflater.inflate(R.layout.item_child_medicine_plan_for_index, null);
            viewHolder1=new ViewHolder1(convertView);
            convertView.setTag(viewHolder1);

        }else {
            viewHolder1=(ViewHolder1)convertView.getTag();
        }
        int medicineType=list.get(groupPosition).getDatas().get(position).getMedicineType();
        String unitfordose=null;
        switch (medicineType) {
            case 0:
                unitfordose = "片";
                break;
            case 1:
                unitfordose = "粒/颗";
                break;
            case 2:
                unitfordose = "瓶/支";
                break;
            case 3:
                unitfordose = "包/袋";
                break;
            case 4:
                unitfordose = "克";
                break;
            case 5:
                unitfordose = "毫升";
                break;
            case 6:
                unitfordose = "其他";
                break;
        }
        //viewHolder1.textView.setTextSize(20);
        //viewHolder1.textView.setTextColor(Color.DKGRAY);
        viewHolder1.medicineName.setText("  " + list.get(groupPosition).getDatas().get(position).getMedicineName());
        viewHolder1.medicineUseCount.setText("  " + list.get(groupPosition).getDatas().get(position).getMedicineUseCount()+unitfordose);
        return convertView;
    }

    class ViewHolder1 {
        private AppCompatTextView medicineName;
        private AppCompatTextView medicineUseCount;
        public ViewHolder1(View view){
            medicineName=(AppCompatTextView ) view.findViewById(R.id.tv_medicine_plan_medicine_name);
            medicineUseCount=(AppCompatTextView ) view.findViewById(R.id.tv_medicine_plan_medicine_count);

        }
    }
    // 删除接口回调方法
    private OnClickDeleteListener onClickDeleteListener = null;
    public void setOnClickDeleteListener(OnClickDeleteListener listener) {
        this.onClickDeleteListener = listener;
    }
    @Override
    public int getChildrenCount(int arg0) {
        // TODO Auto-generated method stub
        return (list!=null && list.size()>0)? list.get(arg0).getDatas().size() : 0;
    }

    @Override
    public Object getGroup(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return (list!=null && list.size()>0) ? list.size() : 0;
    }
    @Override
    public long getGroupId(int arg0) {
        return arg0;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView ==null){

            LayoutInflater inflater = ((Activity)Latte.getConfiguration(ConfigKeys.ACTIVITY)).getLayoutInflater();
            convertView= inflater.inflate(R.layout.item_parent_medicine_plan, null);
            viewHolder =new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.textView.setText(":"+list.get(groupPosition).getTimeString());
        viewHolder.planGroup.setText("计划"+(groupPosition+1));
       /* viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"第一层点击操作",Toast.LENGTH_LONG).show();
            }
        });*/
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }



    class ViewHolder {
        TextView textView;
        TextView planGroup;
        public ViewHolder(View view){
            textView= (TextView) view.findViewById(R.id.parent_title);
            planGroup=(TextView) view.findViewById(R.id.tv_group);
        }
    }
}
