package com.dtmining.latte.app;

import com.joanzapata.iconify.IconFontDescriptor;
import com.joanzapata.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * author:songwenming
 * Date:2018/9/20 0020
 * Description:管理各种配置项目，配置文件的存储和获取
 *
 */
public class Configurator {
    private static final HashMap<Object,Object> LATTE_CONFIGS=new HashMap<>();
    private static final ArrayList<IconFontDescriptor> ICONS= new ArrayList<>();

    private Configurator() {
        LATTE_CONFIGS.put(ConfigKeys.CONFIG_READY,false);
    }

    //静态内部类实现单例模式
    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }
    public static Configurator getInstance(){
        return Holder.INSTANCE;
    }

    final HashMap<Object,Object> getLatteConfigs(){
        return LATTE_CONFIGS;
    }

    public final void configure(){
        initIcons();
        LATTE_CONFIGS.put(ConfigKeys.CONFIG_READY,true);

    }

    public final Configurator withApiHost(String host){
        LATTE_CONFIGS.put(ConfigKeys.API_HOST,host);
        return this;
    }

    private void initIcons(){
        if(ICONS.size()>0){
            //with(params)params=The IconDescriptor holding the ttf file reference and its mappings
            final Iconify.IconifyInitializer initializer=Iconify.with(ICONS.get(0));
            for (int i = 1; i <ICONS.size() ; i++) {
                initializer.with(ICONS.get(i));

            }
        }
    }
    private void checkConfiguration(){
        final boolean isReady=(boolean)LATTE_CONFIGS.get(ConfigKeys.CONFIG_READY);
        if(!isReady){
            throw new RuntimeException("Configuration is not ready ,call configure");
        }
    }

    public final Configurator withIcon(IconFontDescriptor descriptor){
        ICONS.add(descriptor);
        return this;
    }
    //由于配置文件存储的是Object，所以用泛型返回，免去了Object类型转换
    final <T> T getConfiguration(Enum<ConfigKeys> key){
        checkConfiguration();
        return (T)LATTE_CONFIGS.get(key);
    }
}
