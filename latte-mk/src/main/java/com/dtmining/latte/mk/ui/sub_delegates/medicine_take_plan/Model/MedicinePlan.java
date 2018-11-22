package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model;

import java.util.ArrayList;

/**
 * author:songwenming
 * Date:2018/11/22
 * Description:
 */
public class MedicinePlan {
    ArrayList<TimeCountPair> pairs=null;

    public ArrayList<TimeCountPair> getPairs() {
        return pairs;
    }

    public void setPairs(ArrayList<TimeCountPair> pairs) {
        this.pairs = pairs;
    }
    public void addPair(TimeCountPair pair){
        if(pairs==null)
        {
            pairs=new ArrayList<>();
        }
        pairs.add(pair);
    }
}
