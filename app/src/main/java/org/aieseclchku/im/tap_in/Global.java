package org.aieseclchku.im.tap_in;

import android.app.Application;

/**
 * Created by Alpha on 01/01/2015.
 */
public class Global extends Application {

    private String CurrEvent="";
    public String getCurrEvent() {
        return CurrEvent;
    }
    public void setCurrEvent(String str) {
        CurrEvent = str;
    }

    /**public boolean CurrEventSet(){
        return (CurrEvent=="");
    }*/
}