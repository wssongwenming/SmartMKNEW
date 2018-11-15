package com.dtmining.latte.wechat.templates;

/**
 * author:songwenming
 * Date:2018/9/20 0020
 * Description:
 *
 */
import android.util.Log;

import com.dtmining.latte.wechat.BaseWXEntryActivity;
import com.dtmining.latte.wechat.LatteWeChat;

public class WXEntryTemplate  extends BaseWXEntryActivity {

    @Override
    protected void onResume() {
        super.onResume();
        finish();
        //0表示没有动画
        overridePendingTransition(0,0);
    }

    @Override
    protected void onSignInSuccess(String userInfo) {
        Log.d("we", userInfo);
        LatteWeChat.getInstancee().getSignInCallback().onSignInSuccess(userInfo);

    }
    @Override
    protected void onGetOpenIdSuccess(String openId) {
        Log.d("wechat", openId);
        LatteWeChat.getInstancee().getmOpenIdCallback().onGetOpenIdSuccess(openId);
    }
}
