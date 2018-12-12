package com.dtmining.latte.mk.main.aboutme.list;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dtmining.latte.app.AccountManager;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.main.aboutme.medicineboxbind.BoxBindDelegate;
import com.dtmining.latte.mk.main.aboutme.mymedicineboxes.MedicineBoxesMineDelegate;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.aboutme.usermessage.UserMessageDelegate;
import com.dtmining.latte.mk.sign.ISignListener;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineTakeHistoryDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.ActivityManager;
import com.dtmining.latte.util.DataCleanManager;
import com.google.gson.JsonObject;
import com.hmy.popwindow.PopItemAction;
import com.hmy.popwindow.PopWindow;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/10/21
 * Description:
 */
public class ListAdapter extends BaseMultiItemQuickAdapter<ListBean,BaseViewHolder> {
    private String tel;
    private final LatteDelegate DELEGATE;
    private static final RequestOptions OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .dontAnimate()
            .centerCrop();

    public ListAdapter(List<ListBean> data, LatteDelegate delegate,ISignListener signListener,String tel) {
        super(data);
        this.DELEGATE=delegate;
        this.tel=tel;
        addItemType(ListItemType.ITEM_NORMAL, R.layout.arrow_item_layout);
        addItemType(ListItemType.ITEM_WITH_IMAGE, R.layout.arrow_item_with_image);
        addItemType(ListItemType.ITEM_AVATAR, R.layout.arrow_item_avatar);
        addItemType(ListItemType.ITEM_SWITCH,R.layout.arrow_switch_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListBean item) {
        switch (helper.getItemViewType()) {
            case ListItemType.ITEM_NORMAL:
                helper.setText(R.id.tv_arrow_text, item.getText());
                helper.setText(R.id.tv_arrow_value, item.getValue());

                break;
            case ListItemType.ITEM_WITH_IMAGE:
                helper.setText(R.id.arrow_tv_text_value, item.getText());
                helper.setImageResource(R.id.arrow_img_icon, item.getmImage());
                final int id=item.getId();
                View view=helper.itemView;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showContent(id);
                    }
                });
                break;
            case ListItemType.ITEM_AVATAR:
                Glide.with(mContext)
                        .load(item.getImageUrl())
                        .apply(OPTIONS)
                        .into((ImageView) helper.getView(R.id.img_arrow_avatar));
                break;
            case ListItemType.ITEM_SWITCH:
                helper.setText(R.id.tv_arrow_switch_text,item.getText());
                final SwitchCompat switchCompat = helper.getView(R.id.list_item_switch);
                switchCompat.setChecked(true);
                switchCompat.setOnCheckedChangeListener(item.getmOnCheckedChangeListener());
                break;
            default:
                break;
        }
    }

    private void showContent(int Id){

        switch (Id)
        {
            case 1://点击了“添加药箱”
                DELEGATE.start(new AddMedicineBoxDelegate());
                break;
            case 2://点击了“我的药箱”
                DELEGATE.start(new MedicineBoxesMineDelegate());
                break;
            case 3://点击了“绑定当前药箱”
                DELEGATE.start(new BoxBindDelegate());
                break;
            case 4://点击了“删除药箱”
                //DELEGATE.start(new BoxDeleteDelegate());
                break;
            case 5://点击了“用药记录”
                DELEGATE.start(new MedicineTakeHistoryDelegate());
                break;
            case 6://点击了“用户消息”
                DELEGATE.start(new UserMessageDelegate());
                break;
            case 7://点击了“反馈”
                final View customView = View.inflate((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY), R.layout.delegate_user_feedback_pop, null);
                PopWindow popWindow = new PopWindow.Builder((Activity) Latte.getConfiguration(ConfigKeys.ACTIVITY))
                        .setStyle(PopWindow.PopWindowStyle.PopUp)
                        .setTitle("反馈意见")
                        .addContentView(customView)
                        .addItemAction(new PopItemAction("确定", PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
                            @Override
                            public void onClick() {
                                AppCompatEditText mMessage= (AppCompatEditText) customView.findViewById(R.id.et_feedback_message);
                                String message=mMessage.getText().toString();
                                if(!message.isEmpty())
                                {
                                    JsonObject detail=new JsonObject();
                                    detail.addProperty("responseinfo",message);
                                    detail.addProperty("tel",tel);
                                    JsonObject messageJson=new JsonObject();
                                    messageJson.add("detail",detail);
                                    RestClient.builder()
                                            .clearParams()
                                            .url(UploadConfig.API_HOST+"/api/response_info")
                                            .raw(messageJson.toString())
                                            .success(new ISuccess() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    int code= JSON.parseObject(response).getIntValue("code");
                                                    if(code==1){
                                                        Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY),"信息反馈成功",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            })
                                            .build()
                                            .post();

                                }
                                else
                                {Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY),"信息反馈成功",Toast.LENGTH_LONG).show();

                                    mMessage.setError("请输入反馈信息");
                                }
                            }
                        }))
                        .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                        .create();
                popWindow.show();
                break;
            case 8://点击了“清除缓冲”
                DataCleanManager.cleanApplicationData(DELEGATE.getContext());
                Toast.makeText(DELEGATE.getContext(),"缓冲区清除成功",Toast.LENGTH_LONG).show();
                break;
            case 9://点击了“退出”
                AccountManager.setSignState(false);
                ActivityManager.getInstance().finishActivitys();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                break;


        }
    }
}
