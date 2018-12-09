package com.dtmining.latte.mk.main.aboutme.profile;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.main.aboutme.AboutMeDelegate;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.date.DateDialogUtil;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.log.LatteLogger;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class UserProfileClickListener extends SimpleClickListener {

    private final UserProfileDelegate DELEGATE;
    private UserInfo userInfo;
    private String[] mGenders = new String[]{"男", "女", "保密"};
    private String[] mRoles=new String[]{"病人", "医生", "家属"};
    public UserProfileClickListener(UserProfileDelegate DELEGATE,UserInfo userInfo) {
        this.DELEGATE = DELEGATE;
        this.userInfo=userInfo;

    }
    //返回键监听实现
    public interface RefreshListener {
        void onRefresh();
    }
    private RefreshListener backListener;

    @Override
    public void onItemClick(final BaseQuickAdapter adapter, final View view, int position) {
        final ListBean bean = (ListBean) baseQuickAdapter.getData().get(position);
        final int id = bean.getId();
        switch (id) {
            case 1:
                //开始照相机或选择图片
                CallbackManager.getInstance()
                        .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                            @Override
                            public void executeCallback(Uri args) {
                                LatteLogger.d("ON_CROP", args);
                                final ImageView avatar = (ImageView) view.findViewById(R.id.img_arrow_avatar);
                                Glide.with(DELEGATE)
                                        .load(args)
                                        .into(avatar);
                                RestClient.builder()
                                        .clearParams()
                                        .url(UploadConfig.API_HOST+"/api/fileupload")
                                        .loader(DELEGATE.getContext())
                                        .file(args.getPath())
                                        .success(new ISuccess() {
                                            @Override
                                            public void onSuccess(String response) {
                                                if (response != null) {
                                                    final JSONObject responseObject = JSON.parseObject(response);
                                                    int code = responseObject.getIntValue("code");
                                                    if (code == 1) {
                                                        String imgUrl=responseObject.getString("url");
                                                        JsonObject detail=new JsonObject();
                                                        detail.addProperty("user_image",imgUrl);
                                                        detail.addProperty("tel",userInfo.getTel());
                                                        JsonObject userInfoJson=new JsonObject();
                                                        userInfoJson.add("detail",detail);
                                                        //通知服务器更新信息
                                                        RestClient.builder()
                                                        .clearParams()
                                                        .url(UploadConfig.API_HOST+"/api/UserUpdate")
                                                        .raw(userInfoJson.toString())
                                                        .loader(DELEGATE.getContext())
                                                        .success(new ISuccess() {
                                                            @Override
                                                            public void onSuccess(String response) {
                                                                Log.d("response", response);
                                                                ((AboutMeDelegate)Latte.getConfiguration(ConfigKeys.ABOUNTMEDELEGATE)).onRefresh();
                                                                //((AboutMeDelegate)DELEGATE.getParentDelegate()).onRefresh();
                                                                //获取更新后的用户信息，然后更新本地数据库
                                                                //没有本地数据的APP，每次打开APP都请求API，获取信息
                                                            }
                                                        })
                                                        .build()
                                                        .post();
                                                    }
                                                }
                                            }
                                        })
                                        .build()
                                        .upload();
                            }
                        });
                DELEGATE.startCameraWithCheck();
                break;
            case 2:
                final LatteDelegate nameDelegate = bean.getDelegate();
                Latte.getConfigurator().withDelegate(DELEGATE);
                DELEGATE.start(nameDelegate);
                break;
            case 3:
                getGenderDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        JsonObject detail=new JsonObject();
                        detail.addProperty("tel",userInfo.getTel());
                        detail.addProperty("gender",mGenders[which].toString());
                        JsonObject jsonObject=new JsonObject();
                        jsonObject.add("detail",detail);
                        final TextView textView = (TextView) view.findViewById(R.id.tv_arrow_value);
                        RestClient.builder()
                                .clearParams()
                                .raw(jsonObject.toString())
                                .url(UploadConfig.API_HOST+"/api/UserUpdate")
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        textView.setText(mGenders[which]);
                                        dialog.cancel();
                                    }
                                })
                                .build()
                                .post();

                    }
                });
                break;
            case 4:
                final DateDialogUtil dateDialogUtil = new DateDialogUtil();
                dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
                    @Override
                    public void onDateChange(final String date) {
                        String tel=userInfo.getTel();
                        String birthday=date.replace("年","-").replace("月","-").replace("日","");
                        JsonObject detail=new JsonObject();
                        detail.addProperty("tel",tel);
                        detail.addProperty("birthday",birthday);
                        JsonObject jsonObject=new JsonObject();
                        jsonObject.add("detail",detail);
                        RestClient.builder()
                                .clearParams()
                                .raw(jsonObject.toString())
                                .url(UploadConfig.API_HOST+"/api/UserUpdate")
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        final TextView textView = (TextView) view.findViewById(R.id.tv_arrow_value);
                                        textView.setText(date.replace("年","-").replace("月","-").replace("日",""));

                                    }
                                })
                                .build()
                                .post();

                    }
                });
                dateDialogUtil.showDialog(DELEGATE.getContext());
                break;
            case 5:
                getRoleDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final TextView textView = (TextView) view.findViewById(R.id.tv_arrow_value);
                        JsonObject detail=new JsonObject();
                        detail.addProperty("tel",userInfo.getTel());
                        detail.addProperty("role",mRoles[which].toString());
                        JsonObject jsonObject=new JsonObject();
                        jsonObject.add("detail",detail);
                        RestClient.builder()
                                .clearParams()
                                .raw(jsonObject.toString())
                                .url(UploadConfig.API_HOST+"/api/UserUpdate")
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        textView.setText(mRoles[which]);
                                        dialog.cancel();
                                    }
                                })
                                .build()
                                .post();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void getGenderDialog(DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DELEGATE.getContext());
        builder.setSingleChoiceItems(mGenders, 0, listener);
        builder.show();
    }
    private void getRoleDialog(DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DELEGATE.getContext());
        builder.setSingleChoiceItems(mRoles, 0, listener);

        builder.show();
    }


    @Override
    public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
