package com.dtmining.latte.mk.sign;

public interface ISignListener {
    //分别为登录和注册成功的回调
    void onSignInSuccess();
    void onSignUpSuccess();
}
