package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.sign.SignInDelegate;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineMineDelegate extends LatteDelegate {
    private String tel=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_mine;
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
}
