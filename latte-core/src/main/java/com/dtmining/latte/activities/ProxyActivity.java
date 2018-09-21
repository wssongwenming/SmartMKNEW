package com.dtmining.latte.activities;

import com.dtmining.latte.delegates.LatteDelegate;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * author:songwenming
 * Date:2018/9/21
 * Description:该Activity仅仅是作为容器
 */
public abstract class ProxyActivity extends SupportActivity{
    public abstract LatteDelegate setRootDelegate();
}
