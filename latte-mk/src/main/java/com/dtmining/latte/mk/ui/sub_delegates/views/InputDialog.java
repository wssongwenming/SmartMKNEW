package com.dtmining.latte.mk.ui.sub_delegates.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dtmining.latte.mk.R;

/**
 * Created by shikun on 18-6-21.
 */

public class InputDialog extends Dialog{
    private Context context;
    private String msg;
    private String confirmButtonText;
    private ClickListenerInterface mclickListenerInterface;
    private EditText inputeditText;

    public interface ClickListenerInterface {
        public void doConfirm(String input);
    }

    public InputDialog(Context context, String msg, String confirmButtonText , ClickListenerInterface clickListenerInterface) {
        super(context, R.style.Theme_MYDialog);
        this.context = context;
        this.msg = msg;
        this.confirmButtonText = confirmButtonText;
        mclickListenerInterface=clickListenerInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_input_dialog, null);
        setContentView(view);

        TextView tvMsg = (TextView) view.findViewById(R.id.msg);
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);

        inputeditText = (EditText) view.findViewById(R.id.info);

        tvMsg.setText(msg);
        tvConfirm.setText(confirmButtonText);

        tvConfirm.setOnClickListener(new InputDialog.clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }


    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.confirm) {
                mclickListenerInterface.doConfirm(inputeditText.getText().toString());

            }
        }

    };
}
