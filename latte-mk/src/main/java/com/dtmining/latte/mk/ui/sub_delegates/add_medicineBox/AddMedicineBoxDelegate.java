package com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/3
 * Description:
 */
public class AddMedicineBoxDelegate extends LatteDelegate {
    @OnClick(R2.id.btn_add_box_by_hand)
    void AddBoxByHand(){
        start(new AddMedicineBoxByHandDelegate());
    }
    @OnClick(R2.id.btn_add_box_by_scan)
    void AddBoxByScan(){
        startScanWithCheck(this);
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_box_add;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_SCAN, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args) {
                        //Toast.makeText(getContext(),"扫描到的二维码"+args,Toast.LENGTH_LONG).show();
                        AddMedicineBoxByScanDelegate delegate=AddMedicineBoxByScanDelegate.newInstance(args.toString());
                        start(delegate);
                    }
                });
    }
}
