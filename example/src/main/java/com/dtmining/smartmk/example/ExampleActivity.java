package com.dtmining.smartmk.example;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dtmining.latte.activities.ProxyActivity;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;

public class ExampleActivity extends ProxyActivity {
    @Override
    public LatteDelegate setRootDelegate() {
        return new ExampleDelegate();
    }
}
