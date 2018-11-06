package com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/11/3
 * Description:
 */
public class AddMedicineBoxByScanDelegate extends LatteDelegate {
    @BindView(R2.id.box_add_by_san_boxid)
    AppCompatEditText mEtBoxId=null;
    private static final String BOX_ID = "BOX_ID";
    private String mBoxId ="";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mBoxId = args.getString(BOX_ID);
        }
    }
    public static AddMedicineBoxByScanDelegate newInstance(String boxId){
        final Bundle args = new Bundle();
        args.putString(BOX_ID,boxId);
        final AddMedicineBoxByScanDelegate delegate = new AddMedicineBoxByScanDelegate();
        delegate.setArguments(args);
        return delegate;
    }
    private void initData() {
        mEtBoxId.setText(mBoxId);
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_box_add_by_scan;
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        Toast.makeText(getContext(),mBoxId,Toast.LENGTH_LONG).show();
        initData();
    }
}
