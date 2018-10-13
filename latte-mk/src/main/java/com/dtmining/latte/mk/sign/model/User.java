package com.dtmining.latte.mk.sign.model;

import java.io.Serializable;

/**
 * author:songwenming
 * Date:2018/10/4
 * Description:
 */
public class User implements Serializable {
    public String entry_way;
    public String identify_code=null;
    public String pwd=null;
    public String role=null;
    public String tel=null;
    public String username=null;
    public String avatar=null;
    public String weixin_id=null;
    public String qq_id=null;

    public String getEntry_way() {
        return entry_way;
    }

    public String getIdentify_code() {
        return identify_code;
    }

    public String getPwd() {
        return pwd;
    }

    public String getRole() {
        return role;
    }

    public String getTel() {
        return tel;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getWeixin_id() {
        return weixin_id;
    }

    public String getQq_id() {
        return qq_id;
    }

    public void setEntry_way(String entry_way) {
        this.entry_way = entry_way;
    }

    public void setIdentify_code(String identify_code) {
        this.identify_code = identify_code;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setWeixin_id(String weixin_id) {
        this.weixin_id = weixin_id;
    }

    public void setQq_id(String qq_id) {
        this.qq_id = qq_id;
    }
}
