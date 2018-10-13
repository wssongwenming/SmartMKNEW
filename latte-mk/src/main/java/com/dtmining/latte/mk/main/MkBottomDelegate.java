package com.dtmining.latte.mk.main;

import android.graphics.Color;

import com.dtmining.latte.delegates.bottom.BaseBottomDelegate;
import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.delegates.bottom.BottomTabBean;
import com.dtmining.latte.delegates.bottom.ItemBuilder;
import com.dtmining.latte.mk.main.aboutme.AboutMeDelegate;
import com.dtmining.latte.mk.main.index.IndexDelegate;
import com.dtmining.latte.mk.main.manage.ManageDelegate;

import java.util.LinkedHashMap;

/**
 * author:songwenming
 * Date:2018/10/8
 * Description:
 */
public class MkBottomDelegate extends BaseBottomDelegate {
    @Override
    public LinkedHashMap<BottomTabBean, BottomItemDelegate> setItems(ItemBuilder builder) {
        final LinkedHashMap<BottomTabBean,BottomItemDelegate>items=new LinkedHashMap<>();
        items.put(new BottomTabBean("{icon-index}","首页"),new IndexDelegate());
        items.put(new BottomTabBean("{icon-medicinemanage}","药品管理"),new ManageDelegate());
        items.put(new BottomTabBean("{icon-mine}","我的"),new AboutMeDelegate());
        return builder.addItems(items).build();
    }

    @Override
    public int setIndexDelegate() {
        return 0;
    }

    @Override
    public int setClickedColor() {
        return Color.parseColor("#ffff8800");
    }
}
