package org.aieseclchku.im.tap_in;

/**
 * Created by Alpha on 31/12/2014.
 */
public class EventObject {

    String name;
    int id;

    EventObject(String s, int id){
        this.name = s;
        this.id = id;
        }

    EventObject(){
        this.name = "";
        this.id = 0;
    }

    public String toString(){
            return name;
        }

}
