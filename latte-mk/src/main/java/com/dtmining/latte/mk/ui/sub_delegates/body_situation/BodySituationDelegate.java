package com.dtmining.latte.mk.ui.sub_delegates.body_situation;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.ChartUtil;
import com.dtmining.latte.util.Reaction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/11/30
 * Description:
 */
public class BodySituationDelegate extends LatteDelegate {
    private String tel;
    @BindView(R2.id.lineChart)
    LineChart lineChart=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_body_situation;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
    }
    private ArrayList<MultipleItemEntity> getBodySituations(String response){
        BodySituationConverter bodySituationConverter=new BodySituationConverter();
        bodySituationConverter.setJsonData(response);
        return bodySituationConverter.convert();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        RestClient.builder()
                .url("body_situation")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        ArrayList<MultipleItemEntity> multipleItemEntities=getBodySituations(response);
                        List<String> xDataList = new ArrayList<>();// x轴数据源
                        List<Entry> yDataList = new ArrayList<>();// y轴数据数据源
                        int size=multipleItemEntities.size();

                        for (int i=0;i<size;i++){
                            // x轴显示的数据
                            xDataList.add((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_USETIME)+":"+(String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_NAME));
                            //y轴生成float类型的随机数
                            String reaction=multipleItemEntities.get(i).getField(MultipleFields.REACTION);
                            Reaction reactionData=new Reaction();
                            int reaction_int=3;
                            switch (reaction) {
                                case "优":
                                reactionData.setMedicineName((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_NAME));
                                reactionData.setMedicineReaction("优");
                                reactionData.setMedicineUsetime((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_USETIME));
                                reaction_int = 3;
                                break;
                                case "良":
                                    reactionData.setMedicineName((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_NAME));
                                    reactionData.setMedicineReaction("良");
                                    reactionData.setMedicineUsetime((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_USETIME));
                                    reaction_int =2;
                                    break;
                                case "中":
                                    reactionData.setMedicineName((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_NAME));
                                    reactionData.setMedicineReaction("中");
                                    reactionData.setMedicineUsetime((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_USETIME));
                                    reaction_int =1;
                                    break;
                                case "差":
                                    reactionData.setMedicineName((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_NAME));
                                    reactionData.setMedicineReaction("差");
                                    reactionData.setMedicineUsetime((String) multipleItemEntities.get(i).getField(MultipleFields.MEDICINE_USETIME));
                                    reaction_int = 0;
                                    break;
                            }
                            float value = (float) (reaction_int);
                            yDataList.add(new Entry(value, i,reactionData));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            ChartUtil.showChart(getContext(), lineChart, xDataList, yDataList, "0:差 1：中 2：良 3：优", "用药反应/用药时间","kw/h");
                        }
                    }
                })
                .build()
                .get();
    }
}
