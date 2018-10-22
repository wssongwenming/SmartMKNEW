package com.dtmining.latte.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * author:songwenming
 * Date:2018/10/6
 * Description:
 @Entity(nameInDb = "user_profile")
 public class UserProfile {
 @Id
 private long userId=0;
 private String name=null;
 private String avatar=null;
 private String gender=null;
 private String address=null;
 }
 该类经过rebuild会生成余下部分，同时声称DaoSession对象
 */
@Entity(nameInDb = "user_profile")
public class UserProfile {
    //@Id表明userId为唯一Id，不能为int只能为long
    @Id
    private long  tel=0;
    private String username=null;
    private String pwd=null;
    private String role=null;
    private String entry_way=null;
    @Generated(hash = 362390032)
    public UserProfile(long tel, String username, String pwd, String role,
            String entry_way) {
        this.tel = tel;
        this.username = username;
        this.pwd = pwd;
        this.role = role;
        this.entry_way = entry_way;
    }
    @Generated(hash = 968487393)
    public UserProfile() {
    }
    public long getTel() {
        return this.tel;
    }
    public void setTel(long tel) {
        this.tel = tel;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPwd() {
        return this.pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getRole() {
        return this.role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getEntry_way() {
        return this.entry_way;
    }
    public void setEntry_way(String entry_way) {
        this.entry_way = entry_way;
    }
 }



