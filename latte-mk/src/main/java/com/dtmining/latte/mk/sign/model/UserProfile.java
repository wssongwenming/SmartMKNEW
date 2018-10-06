package com.dtmining.latte.mk.sign.model;

/**
 * author:songwenming
 * Date:2018/10/4
 * Description:
 */
public class UserProfile {
    public String entry_way;
    public String identify_code;
    public String pwd;
    public String role;
    public String tel;

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
}
