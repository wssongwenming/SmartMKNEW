package com.dtmining.smartmk.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.IFailure;
import com.dtmining.latte.net.callback.ISuccess;
/**
 * author:songwenming
 * Date:2018/9/22
 * Description:
 */
public class ExampleDelegate extends LatteDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_example;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        //载入fragment后对每个控件做的操作
        testRestClient();
    }
    private void testRestClient(){
        RestClient.builder()
                .url("http://index/")
                .loader(getContext())
                .params("","")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();

                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .build()
                .get();
    }
}
