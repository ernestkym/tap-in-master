package org.aieseclchku.im.tap_in;

/**
 * Created by Alpha on 30/12/2014.
 */
public class RegistrantObject extends PersonObject {
    String attendance = "";
    String remark[] = {"","","",""};

    RegistrantObject(String name, String phone, String ucode, String uid, String email, String attendance, String[] remarks){
        this.name = name;
        this.mobile = phone;
        this.ucode = ucode;
        this.uid = uid;
        this.email = email;
        this.attendance = attendance;
        for (int i = 0; i < 4; i++)
            this.remark[i] = remarks[i];
    }

    RegistrantObject(){ }


}
