package com.dtmining.latte.model;

import java.io.Serializable;

/**
 * author:songwenming
 * Date:2018/9/24
 * Description:
 */
public class User implements Serializable {
    int id;
    String name;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
