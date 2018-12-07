package com.dtmining.MedicalKit.myapplication;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.widget.Toast;

import com.dtmining.latte.activities.ProxyActivity;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.launcher.LauncherDelegate;
import com.dtmining.latte.mk.main.MkBottomDelegate;
import com.dtmining.latte.mk.sign.ISignListener;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.ui.launcher.ILauncherListener;
import com.dtmining.latte.ui.launcher.OnLauncherFinishTag;
import com.dtmining.latte.util.sms.SMSObserver;

import qiu.niorgai.StatusBarCompat;

public class ExampleActivity extends ProxyActivity implements ISignListener,ILauncherListener {

    private SMSObserver mObserver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        //为微信回调保存Activity上下文
        Latte.getConfigurator().withActivity(this);
        StatusBarCompat.translucentStatusBar(this,true);
        //接受短信验证码
        mObserver = new SMSObserver(this,Latte.getHandler());
        Uri uri = Uri.parse("content://sms");
        //第二个参数： 是否监听对应服务所有URI监听  例如sms 所有uri
        getContentResolver().registerContentObserver(uri, true, mObserver);

    }

    @Override
    public LatteDelegate setRootDelegate() {
        //return new ExampleDelegate();
        return new LauncherDelegate();
        //return new LauncherScrollDelegate();
        //return new IndexDelegate();
    }

    @Override
    public void onSignInSuccess() {
        Toast.makeText(this,"登陆成功",Toast.LENGTH_LONG).show();
        startWithPop(new MkBottomDelegate());
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(this,"注册成功",Toast.LENGTH_LONG).show();
        //
        startWithPop(new MkBottomDelegate());
    }

    @Override
    public void onSignInError(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignUpError(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignInFailure(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignUpFailure(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLauncherFinish(OnLauncherFinishTag tag) {
        switch (tag){
            case SIGNED:
                Toast.makeText(this, "启动结束用户已登陆", Toast.LENGTH_SHORT).show();
                startWithPop(new MkBottomDelegate());
                break;
            case NOT_SIGNED:
                Toast.makeText(this, "启动结束用户未登陆", Toast.LENGTH_SHORT).show();
                startWithPop(new SignInDelegate());
                break;
            default:
                break;
        }

    }
}
