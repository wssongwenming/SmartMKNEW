package com.dtmining.smartmk.example;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dtmining.latte.activities.ProxyActivity;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.launcher.LauncherDelegate;
import com.dtmining.latte.mk.launcher.LauncherScrollDelegate;
import com.dtmining.latte.mk.sign.ISignListener;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.sign.SignUpDelegate;
import com.dtmining.latte.ui.launcher.ILauncherListener;
import com.dtmining.latte.ui.launcher.OnLauncherFinishTag;

public class ExampleActivity extends ProxyActivity implements ISignListener,ILauncherListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        //为微信回调保存Activity上下文
        Latte.getConfigurator().withActivity(this);
    }

    @Override
    public LatteDelegate setRootDelegate() {
        //return new ExampleDelegate();
        return new LauncherDelegate();
        //return new LauncherScrollDelegate();
        //return new SignUpDelegate();
    }

    @Override
    public void onSignInSuccess() {
        Toast.makeText(this,"登陆成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(this,"注册成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLauncherFinish(OnLauncherFinishTag tag) {
        switch (tag){
            case SIGNED:
                Toast.makeText(this, "启动结束用户已登陆", Toast.LENGTH_SHORT).show();
                startWithPop(new ExampleDelegate());

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
