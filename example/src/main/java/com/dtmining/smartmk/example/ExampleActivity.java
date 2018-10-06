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
import com.dtmining.latte.mk.sign.SignUpDelegate;

public class ExampleActivity extends ProxyActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    @Override
    public LatteDelegate setRootDelegate() {
        //return new ExampleDelegate();
        //return new LauncherDelegate();
        //return new LauncherScrollDelegate();
        return new SignUpDelegate();
    }
}
