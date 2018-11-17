package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author:songwenming
 * Date:2018/10/31
 * Description:
 */
public class MedicinePlanExpandableListViewAdapter extends BaseExpandableListAdapter {
    private String jsonString;
    private static Map<String, List<MedicinePlan>> dataset = new HashMap<>();
    private static ArrayList<String> parentList=new ArrayList<>();
    private Set<SwipeListLayout> sets=null;

    public MedicinePlanExpandableListViewAdapter(String reponse,Set<SwipeListLayout> sets) {
        jsonString=reponse;
        this.sets=sets;
    }
    public static void convert(String jsonString){
        final JSONObject jsonObject= JSON.parseObject(jsonString);
        //String tel=jsonObject.getString("tel");
        final com.alibaba.fastjson.JSONObject data= jsonObject.getJSONObject("detail");
        final JSONArray dataArray=data.getJSONArray("planlist");
        int size=dataArray.size();
        for (int i = 0; i <size ; i++) {
            JSONObject jsondata= (JSONObject) dataArray.get(i);
            parentList.add(jsondata.getString("time"));
            JSONArray jsonArray=jsondata.getJSONArray("plan");
            int lenght=jsonArray.size();
            List<MedicinePlan> childrenList = new ArrayList<>();
            for (int j = 0; j <lenght ; j++) {
                JSONObject jsonObject1= (JSONObject) jsonArray.get(j);
                MedicinePlan medicinePlanModel=new MedicinePlan();
                medicinePlanModel.setAtime(jsonObject1.getString("attime"));
                medicinePlanModel.setMedicineUseCount(jsonObject1.getString("medicine_useCount"));
                medicinePlanModel.setMedicineName(jsonObject1.getString("medicine_name"));
                medicinePlanModel.setMedicineId(jsonObject1.getString("medicineId"));
                childrenList.add(medicinePlanModel);
            }
            dataset.put(parentList.get(i),childrenList);
        }
    }

    @Override

    public int getGroupCount() {
        if (dataset == null) {
          return 0;
        }
        return dataset.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (dataset.get(parentList.get(groupPosition)) == null) {

            return 0;
        }
        return dataset.get(parentList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dataset.get(parentList.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataset.get(parentList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity)Latte.getConfiguration(ConfigKeys.ACTIVITY)).getLayoutInflater();
            convertView= inflater.inflate(R.layout.item_parent_medicine_plan, null);
        }
        convertView.setTag(R.layout.item_parent_medicine_plan, groupPosition);
        convertView.setTag(R.layout.item_child_medicine_plan, -1);
        TextView text = (TextView) convertView.findViewById(R.id.parent_title);
        text.setText(parentList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)Latte.getConfiguration(ConfigKeys.ACTIVITY)).getLayoutInflater();
            convertView= inflater.inflate(R.layout.item_child_medicine_plan, null);
        }
        final SwipeListLayout sll = (SwipeListLayout) convertView.findViewById(R.id.item_child_medicine_plan_sll);
        sll.setOnSwipeStatusListener(new onSlipStatusListener(sll));
        convertView.setTag(R.layout.item_parent_medicine_plan, groupPosition);
        convertView.setTag(R.layout.item_child_medicine_plan, childPosition);
        TextView medicineName = (TextView) convertView.findViewById(R.id.tv_medicine_plan_medicine_name);
        TextView medicineCount= (TextView) convertView.findViewById(R.id.tv_medicine_plan_medicine_count);
        TextView delete = (TextView) convertView.findViewById(R.id.tv_btn_item_child_medicine_plan_delete);
        TextView change = (TextView) convertView.findViewById(R.id.tv_btn_item_child_medicine_plan_change);
        final String medicinename=dataset.get(parentList.get(groupPosition)).get(childPosition).getMedicineName();
        String medicnecount=dataset.get(parentList.get(groupPosition)).get(childPosition).getMedicineUseCount();
        //修改或删除计划时，需要药品标识medicineId
        final String medicineId  =dataset.get(parentList.get(groupPosition)).get(childPosition).getMedicineId();
        medicineCount.setText(medicnecount);
        medicineName.setText(medicinename);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "onClick: ");
                RestClient.builder()
                        .url("http://10.0.2.2:8081/Web01_exec/Delete_plan")
                        .raw(medicinename)//应该传参数medicineId，这里由于medicineId为空,所以暂用medicinename代替
                        .success(new ISuccess() {
                            @Override
                            public void onSuccess(String response) {

                                dataset.get(parentList.get(groupPosition)).remove(childPosition);
                                if(dataset.get(parentList.get(groupPosition)).size()==0)
                                {
                                    dataset.remove(parentList.get(groupPosition));
                                }
                                notifyDataSetChanged();
                            }
                        })
                        .build()
                        .post();

            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestClient.builder()
                        .url("http://10.0.2.2:8081/Web01_exec/Delete_plan")
                        .raw(medicinename)//应该传参数medicineId，这里由于medicineId为空,所以暂用medicinename代替
                        .success(new ISuccess() {
                            @Override
                            public void onSuccess(String response) {

                            }
                        })
                        .build()
                        .post();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
