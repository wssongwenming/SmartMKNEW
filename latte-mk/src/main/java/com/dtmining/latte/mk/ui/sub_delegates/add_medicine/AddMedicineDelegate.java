package com.dtmining.latte.mk.ui.sub_delegates.add_medicine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.scan_add.ScanAddDelegate;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/19
 * Description:
 */
public class AddMedicineDelegate extends LatteDelegate {
    @OnClick(R2.id.btn_medicine_add_by_hand)
    void addByHand(){
        start(new HandAddDelegate());
    }
    @OnClick(R2.id.btn_medicine_add_by_scan)
    void addByScan(){
        startScanWithCheck(this);
    }
    @Override
    public Object setLayout() {
        return R.layout.medicine_add;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_SCAN, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args) {
                        //Toast.makeText(getContext(),"扫描到的二维码"+args,Toast.LENGTH_LONG).show();
                        HandAddDelegate delegate=HandAddDelegate.newInstance(args.toString());
                        start(delegate);
                    }
                });
    }
}
