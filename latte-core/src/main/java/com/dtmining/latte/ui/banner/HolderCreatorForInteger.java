package com.dtmining.latte.ui.banner;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;

public class HolderCreatorForInteger implements CBViewHolderCreator<ImageHolderForInteger> {
    @Override
    public ImageHolderForInteger createHolder() {
        return new ImageHolderForInteger();
    }
}
