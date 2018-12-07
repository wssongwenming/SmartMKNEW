package com.dtmining.latte.mk.main.aboutme.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.list.ListAdapter;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.mk.main.aboutme.list.ListItemType;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 */

public class SettingsDelegate extends LatteDelegate {
    @BindView(R2.id.rv_settings)
    RecyclerView recyclerView=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_settings;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

             final ListBean push = new ListBean.Builder()
                .setItemType(ListItemType.ITEM_SWITCH)
                .setId(1)
               // .setDelegate(new AddressDelegate())
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            CallbackManager.getInstance().getCallback(CallbackType.TAG_OPEN_PUSH).executeCallback(null);
                            Toast.makeText(getContext(), "打开推送", Toast.LENGTH_SHORT).show();
                        } else {
                            CallbackManager.getInstance().getCallback(CallbackType.TAG_STOP_PUSH).executeCallback(null);
                            Toast.makeText(getContext(), "关闭推送", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setText("消息推送")
                .build();

        final ListBean about = new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(2)
                .setDelegate(new AboutDelegate())
                .setText("关于")
                .build();

        final List<ListBean> data = new ArrayList<>();
        data.add(push);
        data.add(about);

        //设置RecyclerView
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        final ListAdapter adapter = new ListAdapter(data,this.getParentDelegate(),null,null);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new SettingsClickListener(this));
    }
}
