package com.bs.behrah.batterystatus;

import android.util.Log;


public final class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/roboto_bold.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/roboto_light.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/ir_sans.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/ir_sans_bold.ttf");

    }
}
