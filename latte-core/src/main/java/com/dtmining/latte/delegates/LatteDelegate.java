package com.dtmining.latte.delegates;

/**
 * author:songwenming
 * Date:2018/9/21
 * Description:
 */
public abstract class LatteDelegate extends PermissionCheckerDelegate {
    public <T extends LatteDelegate> T getParentDelegate(){
        return  (T)getParentFragment();
    }
}
