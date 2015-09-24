package org.aieseclchku.im.tap_in;

/**
 * Created by Alpha on 30/12/2014.
 */
public class PersonObject {
    String name = "";
    String mobile = "";
    String ucode = "";
    String uid = "";
    String email = "";
    String remark[] = {"","","",""};
    String department = "";
    String role = "";
    String year = "";
    String NfcTag = "";

    PersonObject(String Uid, String Name, String Mobile, String Email, String Year, String Department, String Role, String NfcTag){
        this.department = Department;
        this.name = Name;
        this.year = Year;
        this.role = Role;
        this.mobile = Mobile;
        this.email = Email;
        this.uid = Uid;
        this.NfcTag = NfcTag;
    }

    PersonObject(String Uid, String Name, String Mobile, String Email, String NfcTag){
        this.name = Name;
        this.mobile = Mobile;
        this.email = Email;
        this.uid = Uid;
        this.NfcTag = NfcTag;
    }

    PersonObject(){ }


}
