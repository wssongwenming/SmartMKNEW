package com.dtmining.latte.delegates.bottom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import com.dtmining.latte.R;
import com.dtmining.latte.R2;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.delegates.bottom.BottomTabBean;
import com.dtmining.latte.delegates.bottom.ItemBuilder;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import me.yokeyword.fragmentation.SupportFragment;
/**
 * author:songwenming
 * Date:2018/10/8
 * Description:
 */
public abstract class BaseBottomDelegate extends LatteDelegate implements View.OnClickListener{
    private final ArrayList<BottomItemDelegate> ITEM_DELEGATES=new ArrayList<>();
    private final ArrayList<BottomTabBean> TAB_BEANS=new ArrayList<>();
    private final LinkedHashMap<BottomTabBean,BottomItemDelegate> ITEMS=new LinkedHashMap<>();
    //当前Fragment是哪一个
    private int mCurrentDelegate=0;
    private int mIndexDelegate=0;
    private int mClickedColor= Color.RED;
    @BindView(R2.id.bottom_bar)
    LinearLayoutCompat mBottomBar=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_bottom;
    }

    //思路，建立父类的抽象方法就是为了在子类中赋值，setItems是起始源头，setItems->ITEMS
    public abstract LinkedHashMap<BottomTabBean,BottomItemDelegate> setItems(ItemBuilder builder);

    public abstract  int setIndexDelegate();
    @ColorInt
    public abstract int setClickedColor();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndexDelegate=setIndexDelegate();
        if(setClickedColor()!=0)
        {
            mClickedColor=setClickedColor();
        }
        final ItemBuilder builder=ItemBuilder.builder();
        //传入builder然后生成Item
        final LinkedHashMap<BottomTabBean,BottomItemDelegate> items=setItems(builder);
        ITEMS.putAll(items);
        for(Map.Entry<BottomTabBean,BottomItemDelegate>item:ITEMS.entrySet()){
            final BottomTabBean key=item.getKey();
            final BottomItemDelegate value=item.getValue();
            TAB_BEANS.add(key);
            ITEM_DELEGATES.add(value);
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        final int size=ITEMS.size();
        for(int i=0;i<size;i++){
            //加载布局管理器,将xml布局转换为view对象,一个一个塞到mBottomBar内
            LayoutInflater.from(getContext()).inflate(R.layout.bottom_item_icon_text_layout,mBottomBar);
            final RelativeLayout item=(RelativeLayout)mBottomBar.getChildAt(i);
            //设置每个item的点击事件
            item.setTag(i);
            item.setOnClickListener(this);
            final IconTextView itemIcon= (IconTextView) item.getChildAt(0);
            final AppCompatTextView itemTitle= (AppCompatTextView) item.getChildAt(1);
            final BottomTabBean bean=TAB_BEANS.get(i);
            //初始化数据
            itemIcon.setText(bean.getIcon());
            itemTitle.setText(bean.getTitle());
            if(i==mIndexDelegate)
            {
                itemIcon.setTextColor(mClickedColor);
                itemTitle.setTextColor(mClickedColor);
            }
        }
        //@param a the array into which the elements of the list are to
        // be stored, if it is big enough; otherwise, a new array of the
        // same runtime type is allocated for this purpose.
        // @return an array containing the elements of the list
        //public <T> T[] toArray(T[] a)
        final SupportFragment[] delegateArray=ITEM_DELEGATES.toArray(new SupportFragment[size]);
        loadMultipleRootFragment(R.id.bottom_bar_delegate_container,mIndexDelegate,delegateArray);
    }
    private void resetColor(){
        final int count=mBottomBar.getChildCount();
        for(int i=0;i<count;i++)
        {
            final RelativeLayout item=(RelativeLayout)mBottomBar.getChildAt(i);
            final IconTextView itemIcon=(IconTextView)item.getChildAt(0);
            itemIcon.setTextColor(Color.GRAY);
            final AppCompatTextView itemTitle=(AppCompatTextView)item.getChildAt(1);
            itemTitle.setTextColor(Color.GRAY);

        }
    }

    @Override
    public void onClick(View v) {
        final int tag=(int)v.getTag();
        resetColor();
        final RelativeLayout item=(RelativeLayout) v;
        final IconTextView itemIcon=(IconTextView)item.getChildAt(0);
        itemIcon.setTextColor(mClickedColor);
        final AppCompatTextView itemTitle=(AppCompatTextView)item.getChildAt(1);
        itemTitle.setTextColor(mClickedColor);
        //show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
        showHideFragment(ITEM_DELEGATES.get(tag),ITEM_DELEGATES.get(mCurrentDelegate));
        mCurrentDelegate=tag;
    }
}
