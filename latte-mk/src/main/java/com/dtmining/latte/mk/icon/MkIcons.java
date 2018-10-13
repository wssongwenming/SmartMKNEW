package com.dtmining.latte.mk.icon;

import com.joanzapata.iconify.Icon;

public enum  MkIcons implements Icon {
    icon_index('\ue900'),
    icon_qq('\ue901'),
    icon_wechat('\ue903'),
    icon_medicinemanage('\ue902'),
    icon_mine('\ue904');
    ;
    private char character;
    MkIcons(char character) {
        this.character = character;
    }
    @Override
    public String key() {
        return name().replace('_','-');
    }
    @Override
    public char character() {
        return character;
    }
}
