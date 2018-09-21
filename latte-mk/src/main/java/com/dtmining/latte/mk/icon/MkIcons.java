package com.dtmining.latte.mk.icon;

import com.joanzapata.iconify.Icon;

public enum  MkIcons implements Icon {
    icon_qq('\ue901'),
    icon_blog('\ue902');
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
