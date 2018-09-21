package com.dtmining.latte.mk.icon;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconFontDescriptor;

public class FontMkModule implements IconFontDescriptor {
    @Override
    public String ttfFileName() {
        return "icon.ttf";
    }

    @Override
    public Icon[] characters() {
        return MkIcons.values();
    }
}
