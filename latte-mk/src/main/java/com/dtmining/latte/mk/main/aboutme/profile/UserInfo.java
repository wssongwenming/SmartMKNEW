package com.dtmining.latte.mk.main.aboutme.profile;

/**
 * author:songwenming
 * Date:2018/12/8
 * Description:
 */
public class UserInfo {
      private String tel;
      private String username;
      private String role;
      private String gender;
      private String birthday;
      private String user_image;

    public String getTel() {
        return tel;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
